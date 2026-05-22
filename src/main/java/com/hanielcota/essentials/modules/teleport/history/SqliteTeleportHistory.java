package com.hanielcota.essentials.modules.teleport.history;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.database.Sql;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class SqliteTeleportHistory implements TeleportHistory, AutoCloseable {

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
   * Single-threaded writer for history mutations. Teleport events fire on the server thread; the
   * SQLite INSERT/DELETE must not block it on disk I/O. Reads ({@link #list}) stay synchronous —
   * they only run on the rare {@code /back} command, not on every teleport.
   */
  private final AsyncDatabaseWriter writer;

  public SqliteTeleportHistory(DatabaseProvider database) {
    this.database = Objects.requireNonNull(database, "database");
    this.writer = new AsyncDatabaseWriter("Essentialist-TeleportHistory");
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

    writer.submit(
        "push",
        () ->
            Sql.tx(
                database,
                conn -> {
                  Sql.execute(conn, INSERT, playerId, worldName, x, y, z, yaw, pitch, createdAt);
                  Sql.execute(conn, TRIM, playerId, playerId, CAPACITY);
                }));
  }

  @Override
  public List<HistoryEntry> list(UUID player) {
    Objects.requireNonNull(player, "player");
    return Sql.query(database, LIST, SqliteTeleportHistory::readEntry, player.toString(), CAPACITY);
  }

  @Override
  public void remove(UUID player, long entryId) {
    Objects.requireNonNull(player, "player");
    if (entryId <= 0) {
      return;
    }
    String playerId = player.toString();
    writer.submit("remove", () -> Sql.update(database, DELETE_BY_ID, entryId, playerId));
  }

  @Override
  public void close() {
    writer.close();
  }
}
