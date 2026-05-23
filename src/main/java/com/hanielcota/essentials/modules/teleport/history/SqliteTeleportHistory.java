package com.hanielcota.essentials.modules.teleport.history;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlExecutor;
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
      """
      CREATE INDEX IF NOT EXISTS idx_teleport_history_player \
      ON teleport_history(player_id, created_at)\
      """;

  private static final String INSERT =
      """
      INSERT INTO teleport_history (player_id, world, x, y, z, yaw, pitch, created_at) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)\
      """;

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
      """
      SELECT id, world, x, y, z, yaw, pitch, created_at FROM teleport_history \
      WHERE player_id = ? ORDER BY created_at DESC LIMIT ?\
      """;

  private static final String DELETE_BY_ID =
      """
      DELETE FROM teleport_history WHERE id = ? AND player_id = ?\
      """;

  private final SqlExecutor sqlExecutor;

  /**
   * Single-threaded writer for history mutations. Teleport events fire on the server thread; the
   * SQLite INSERT/DELETE must not block it on disk I/O. Reads ({@link #list}) stay synchronous —
   * they only run on the rare {@code /back} command, not on every teleport.
   */
  private final AsyncDatabaseWriter writer;

  public SqliteTeleportHistory(SqlExecutor sqlExecutor) {
    this.sqlExecutor = Objects.requireNonNull(sqlExecutor, "sqlExecutor");
    this.writer = new DefaultAsyncDatabaseWriter("Essentialist-TeleportHistory");
    sqlExecutor.ddl(CREATE_TABLE, CREATE_INDEX);
  }

  /**
   * Maps one row from the {@code teleport_history} table to a {@link HistoryEntry}.
   *
   * <p>Returns {@code null} when the row's world is no longer loaded on the server. {@link
   * com.hanielcota.essentials.database.Sql#query} filters {@code null} mappings out of the result
   * list, so a stale row produces a silently-dropped entry rather than a crash.
   */
  private static HistoryEntry readEntry(ResultSet rs) throws SQLException {
    var id = rs.getLong("id");
    var worldName = rs.getString("world");
    var world = Bukkit.getWorld(worldName);
    if (world == null) {
      return null;
    }
    var x = rs.getDouble("x");
    var y = rs.getDouble("y");
    var z = rs.getDouble("z");
    var yaw = (float) rs.getDouble("yaw");
    var pitch = (float) rs.getDouble("pitch");
    var loc = new Location(world, x, y, z, yaw, pitch);
    var createdAt = rs.getLong("created_at");
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
    var playerId = player.toString();
    var worldName = world.getName();
    var x = location.getX();
    var y = location.getY();
    var z = location.getZ();
    var yaw = location.getYaw();
    var pitch = location.getPitch();
    var createdAt = System.currentTimeMillis();

    writer.submit(
        "push",
        () ->
            sqlExecutor.tx(
                conn -> {
                  sqlExecutor.execute(
                      conn, INSERT, playerId, worldName, x, y, z, yaw, pitch, createdAt);
                  sqlExecutor.execute(conn, TRIM, playerId, playerId, CAPACITY);
                }));
  }

  @Override
  public List<HistoryEntry> list(UUID player) {
    Objects.requireNonNull(player, "player");
    return sqlExecutor.query(LIST, SqliteTeleportHistory::readEntry, player.toString(), CAPACITY);
  }

  @Override
  public void remove(UUID player, long entryId) {
    Objects.requireNonNull(player, "player");
    if (entryId <= 0) {
      return;
    }
    var playerId = player.toString();
    writer.submit("remove", () -> sqlExecutor.update(DELETE_BY_ID, entryId, playerId));
  }

  @Override
  public void close() {
    writer.close();
  }
}
