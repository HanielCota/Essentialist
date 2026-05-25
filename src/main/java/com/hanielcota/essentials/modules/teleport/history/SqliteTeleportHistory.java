package com.hanielcota.essentials.modules.teleport.history;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlExecutor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class SqliteTeleportHistory implements TeleportHistory, AutoCloseable {

  private final SqlExecutor sqlExecutor;

  /**
   * Single-threaded writer for history mutations. Teleport events fire on the server thread; the
   * SQLite INSERT/DELETE must not block it on disk I/O. Reads ({@link #list}) stay synchronous —
   * they only run on the rare {@code /back} command, not on every teleport.
   */
  private final AsyncDatabaseWriter writer;

  public SqliteTeleportHistory(SqlExecutor sqlExecutor) {
    this.sqlExecutor = sqlExecutor;
    this.writer = new DefaultAsyncDatabaseWriter("Essentialist-TeleportHistory");
  }

  /**
   * Maps one row from the {@code teleport_history} table to a {@link HistoryEntry}.
   *
   * <p>Returns {@code null} when the row's world is no longer loaded on the server. {@link
   * com.hanielcota.essentials.database.Sql#query} filters {@code null} mappings out of the result
   * list, so a stale row produces a silently-dropped entry rather than a crash.
   */
  private static HistoryEntry readEntry(@NonNull ResultSet rs) throws SQLException {
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
    var location = new Location(world, x, y, z, yaw, pitch);

    var createdAt = rs.getLong("created_at");

    return new HistoryEntry(id, location, createdAt);
  }

  @Override
  public void push(@NonNull UUID player, @NonNull Location location) {
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

    Runnable insertAndTrim = () -> writeEntry(playerId, worldName, x, y, z, yaw, pitch, createdAt);
    this.writer.submit("push", insertAndTrim);
  }

  private void writeEntry(
      String playerId,
      String worldName,
      double x,
      double y,
      double z,
      float yaw,
      float pitch,
      long createdAt) {
    this.sqlExecutor.tx(
        conn -> insertAndTrim(conn, playerId, worldName, x, y, z, yaw, pitch, createdAt));
  }

  private void insertAndTrim(
      Connection conn,
      String playerId,
      String worldName,
      double x,
      double y,
      double z,
      float yaw,
      float pitch,
      long createdAt)
      throws SQLException {
    this.sqlExecutor.execute(
        conn, TeleportHistoryTable.INSERT, playerId, worldName, x, y, z, yaw, pitch, createdAt);
    this.sqlExecutor.execute(conn, TeleportHistoryTable.TRIM, playerId, playerId, CAPACITY);
  }

  @Override
  public List<HistoryEntry> list(@NonNull UUID player) {
    var playerId = player.toString();
    return this.sqlExecutor.query(
        TeleportHistoryTable.LIST, SqliteTeleportHistory::readEntry, playerId, CAPACITY);
  }

  @Override
  public void remove(@NonNull UUID player, long entryId) {
    if (entryId <= 0) {
      return;
    }

    var playerId = player.toString();
    Runnable deleteEntry =
        () -> this.sqlExecutor.update(TeleportHistoryTable.DELETE_BY_ID, entryId, playerId);

    this.writer.submit("remove", deleteEntry);
  }

  @Override
  public void close() {
    this.writer.close();
  }
}
