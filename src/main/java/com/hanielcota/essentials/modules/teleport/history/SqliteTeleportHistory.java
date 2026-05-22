package com.hanielcota.essentials.modules.teleport.history;

import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.database.Sql;
import com.hanielcota.essentials.util.Log;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class SqliteTeleportHistory implements TeleportHistory, AutoCloseable {

  private static final Log LOG = Log.of(SqliteTeleportHistory.class);

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS teleport_history (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        player_id TEXT NOT NULL,
        world TEXT NOT NULL,
        x REAL NOT NULL,
        y REAL NOT NULL,
        z REAL NOT NULL,
        yaw REAL NOT NULL,
        pitch REAL NOT NULL,
        created_at INTEGER NOT NULL
      )
      """;

  private static final String CREATE_INDEX =
      "CREATE INDEX IF NOT EXISTS idx_teleport_history_player "
          + "ON teleport_history(player_id, created_at)";

  private static final String INSERT =
      "INSERT INTO teleport_history (player_id, world, x, y, z, yaw, pitch, created_at) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

  private static final String TRIM =
      """
      DELETE FROM teleport_history
      WHERE player_id = ?
        AND id NOT IN (
          SELECT id FROM teleport_history
          WHERE player_id = ?
          ORDER BY created_at DESC
          LIMIT ?
        )
      """;

  private static final String LIST =
      "SELECT id, world, x, y, z, yaw, pitch, created_at FROM teleport_history "
          + "WHERE player_id = ? ORDER BY created_at DESC LIMIT ?";

  private static final String DELETE_BY_ID =
      "DELETE FROM teleport_history WHERE id = ? AND player_id = ?";

  private final DatabaseProvider database;

  /**
   * Single-threaded executor for history writes. Teleport events fire on the server thread; running
   * the SQLite INSERT/DELETE there would block the main thread on disk I/O. Reads ({@link #list})
   * stay synchronous — they only happen on the rare {@code /back} command, not on every teleport.
   */
  private final ExecutorService writeExecutor;

  public SqliteTeleportHistory(DatabaseProvider database) {
    this.database = Objects.requireNonNull(database, "database");
    this.writeExecutor =
        Executors.newSingleThreadExecutor(
            runnable -> {
              var thread = new Thread(runnable, "Essentialist-TeleportHistory");
              thread.setDaemon(true);
              return thread;
            });
    Sql.ddl(database, CREATE_TABLE, CREATE_INDEX);
  }

  /**
   * Maps one row from the {@code teleport_history} table to a {@link HistoryEntry}.
   *
   * <p>Returns {@code null} when the row's world is no longer loaded on the server. {@link
   * com.hanielcota.essentials.database.Sql#query} filters {@code null} mappings out of the result
   * list, so a stale row produces a silently-dropped entry rather than a crash.
   */
  private static HistoryEntry readEntry(ResultSet rs) throws SQLException {
    long id = rs.getLong("id");
    String worldName = rs.getString("world");
    var world = Bukkit.getWorld(worldName);
    if (world == null) {
      return null;
    }
    double x = rs.getDouble("x");
    double y = rs.getDouble("y");
    double z = rs.getDouble("z");
    float yaw = (float) rs.getDouble("yaw");
    float pitch = (float) rs.getDouble("pitch");
    var loc = new Location(world, x, y, z, yaw, pitch);
    long createdAt = rs.getLong("created_at");
    return new HistoryEntry(id, loc, createdAt);
  }

  @Override
  public void push(UUID player, Location location) {
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(location, "location");
    var world = location.getWorld();
    if (world == null) {
      return;
    }
    // Snapshot the (mutable) Location's values on the calling thread before handing off.
    String playerId = player.toString();
    String worldName = world.getName();
    double x = location.getX();
    double y = location.getY();
    double z = location.getZ();
    float yaw = location.getYaw();
    float pitch = location.getPitch();
    long createdAt = System.currentTimeMillis();

    submit(
        "push",
        () ->
            Sql.tx(
                database,
                conn -> {
                  try (PreparedStatement insert = conn.prepareStatement(INSERT)) {
                    insert.setString(1, playerId);
                    insert.setString(2, worldName);
                    insert.setDouble(3, x);
                    insert.setDouble(4, y);
                    insert.setDouble(5, z);
                    insert.setDouble(6, yaw);
                    insert.setDouble(7, pitch);
                    insert.setLong(8, createdAt);
                    insert.executeUpdate();
                  }
                  try (PreparedStatement trim = conn.prepareStatement(TRIM)) {
                    trim.setString(1, playerId);
                    trim.setString(2, playerId);
                    trim.setInt(3, CAPACITY);
                    trim.executeUpdate();
                  }
                }));
  }

  @Override
  public List<HistoryEntry> list(UUID player) {
    Objects.requireNonNull(player, "player");
    return Sql.query(
        database,
        LIST,
        stmt -> {
          stmt.setString(1, player.toString());
          stmt.setInt(2, CAPACITY);
        },
        SqliteTeleportHistory::readEntry);
  }

  @Override
  public void remove(UUID player, long entryId) {
    Objects.requireNonNull(player, "player");
    if (entryId <= 0) {
      return;
    }
    String playerId = player.toString();
    submit(
        "remove",
        () ->
            Sql.update(
                database,
                DELETE_BY_ID,
                stmt -> {
                  stmt.setLong(1, entryId);
                  stmt.setString(2, playerId);
                }));
  }

  /** Hands a write off to the background executor, logging (never rethrowing) any failure. */
  private void submit(String operation, Runnable work) {
    try {
      writeExecutor.execute(
          () -> {
            try {
              work.run();
            } catch (RuntimeException e) {
              LOG.warn(e, "Async teleport-history {} failed", operation);
            }
          });
    } catch (RejectedExecutionException e) {
      LOG.warn("Teleport-history executor rejected {} (plugin shutting down?)", operation);
    }
  }

  @Override
  public void close() {
    writeExecutor.shutdown();
    try {
      if (!writeExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
        int dropped = writeExecutor.shutdownNow().size();
        LOG.warn("Teleport-history writer did not drain within 5s; {} write(s) dropped", dropped);
      }
    } catch (InterruptedException e) {
      writeExecutor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
