package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.database.SqlExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * SQLite-backed storage of player homes.
 *
 * <p>Primary key is {@code (player_id, name)} (case-insensitive on the name column) so calls to
 * {@link #save} act as upsert per player+name pair.
 */
public final class HomeStore {

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS homes (
        player_id TEXT NOT NULL,
        name TEXT NOT NULL COLLATE NOCASE,
        world TEXT NOT NULL,
        x REAL NOT NULL,
        y REAL NOT NULL,
        z REAL NOT NULL,
        yaw REAL NOT NULL,
        pitch REAL NOT NULL,
        created_at INTEGER NOT NULL,
        PRIMARY KEY (player_id, name)
      )
      """;

  private static final String UPSERT =
      """
      INSERT OR REPLACE INTO homes \
      (player_id, name, world, x, y, z, yaw, pitch, created_at) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)\
      """;

  private static final String DELETE =
      """
      DELETE FROM homes WHERE player_id = ? AND name = ?\
      """;

  private static final String SELECT_ONE =
      """
      SELECT player_id, name, world, x, y, z, yaw, pitch, created_at \
      FROM homes WHERE player_id = ? AND name = ?\
      """;

  private static final String SELECT_ALL =
      """
      SELECT player_id, name, world, x, y, z, yaw, pitch, created_at \
      FROM homes WHERE player_id = ? ORDER BY name\
      """;

  private static final String COUNT =
      """
      SELECT COUNT(*) AS total FROM homes WHERE player_id = ?\
      """;

  private final SqlExecutor sqlExecutor;

  public HomeStore(SqlExecutor sqlExecutor) {
    this.sqlExecutor = Objects.requireNonNull(sqlExecutor, "sqlExecutor");
    sqlExecutor.ddl(CREATE_TABLE);
  }

  public Optional<Home> find(UUID owner, String name) {
    Objects.requireNonNull(owner, "owner");
    Objects.requireNonNull(name, "name");
    var rows = sqlExecutor.query(SELECT_ONE, HomeStore::readRow, owner.toString(), name);
    return rows.isEmpty() ? Optional.empty() : Optional.of(rows.getFirst());
  }

  public List<Home> list(UUID owner) {
    Objects.requireNonNull(owner, "owner");
    return sqlExecutor.query(SELECT_ALL, HomeStore::readRow, owner.toString());
  }

  public int count(UUID owner) {
    Objects.requireNonNull(owner, "owner");
    var counts = sqlExecutor.query(COUNT, rs -> rs.getInt("total"), owner.toString());
    return counts.isEmpty() ? 0 : counts.getFirst();
  }

  public void save(Home home) {
    Objects.requireNonNull(home, "home");
    sqlExecutor.update(
        UPSERT,
        home.owner().toString(),
        home.name(),
        home.world(),
        home.x(),
        home.y(),
        home.z(),
        home.yaw(),
        home.pitch(),
        home.createdAt());
  }

  /** Deletes the home. Returns {@code true} when a row was removed. */
  public boolean delete(UUID owner, String name) {
    Objects.requireNonNull(owner, "owner");
    Objects.requireNonNull(name, "name");
    if (find(owner, name).isEmpty()) {
      return false;
    }
    sqlExecutor.update(DELETE, owner.toString(), name);
    return true;
  }

  private static Home readRow(ResultSet rs) throws SQLException {
    return new Home(
        UUID.fromString(rs.getString("player_id")),
        rs.getString("name"),
        rs.getString("world"),
        rs.getDouble("x"),
        rs.getDouble("y"),
        rs.getDouble("z"),
        (float) rs.getDouble("yaw"),
        (float) rs.getDouble("pitch"),
        rs.getLong("created_at"));
  }
}
