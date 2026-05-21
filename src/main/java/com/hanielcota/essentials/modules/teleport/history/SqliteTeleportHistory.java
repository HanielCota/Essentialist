package com.hanielcota.essentials.modules.teleport.history;

import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.database.Sql;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class SqliteTeleportHistory implements TeleportHistory {

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
      "SELECT id, world, x, y, z, yaw, pitch FROM teleport_history "
          + "WHERE player_id = ? ORDER BY created_at DESC LIMIT ?";

  private static final String DELETE_BY_ID = "DELETE FROM teleport_history WHERE id = ?";

  private final DatabaseProvider database;

  public SqliteTeleportHistory(DatabaseProvider database) {
    this.database = Objects.requireNonNull(database, "database");
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
    World world = Bukkit.getWorld(worldName);
    if (world == null) {
      return null;
    }
    double x = rs.getDouble("x");
    double y = rs.getDouble("y");
    double z = rs.getDouble("z");
    float yaw = (float) rs.getDouble("yaw");
    float pitch = (float) rs.getDouble("pitch");
    Location loc = new Location(world, x, y, z, yaw, pitch);
    return new HistoryEntry(id, loc);
  }

  @Override
  public void push(UUID player, Location location) {
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(location, "location");
    World world = location.getWorld();
    if (world == null) {
      return;
    }
    String playerId = player.toString();
    Sql.tx(
        database,
        conn -> {
          try (PreparedStatement insert = conn.prepareStatement(INSERT)) {
            insert.setString(1, playerId);
            insert.setString(2, world.getName());
            insert.setDouble(3, location.getX());
            insert.setDouble(4, location.getY());
            insert.setDouble(5, location.getZ());
            insert.setDouble(6, location.getYaw());
            insert.setDouble(7, location.getPitch());
            insert.setLong(8, System.currentTimeMillis());
            insert.executeUpdate();
          }
          try (PreparedStatement trim = conn.prepareStatement(TRIM)) {
            trim.setString(1, playerId);
            trim.setString(2, playerId);
            trim.setInt(3, CAPACITY);
            trim.executeUpdate();
          }
        });
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
    Sql.update(
        database,
        DELETE_BY_ID + " AND player_id = ?",
        stmt -> {
          stmt.setLong(1, entryId);
          stmt.setString(2, playerId);
        });
  }
}
