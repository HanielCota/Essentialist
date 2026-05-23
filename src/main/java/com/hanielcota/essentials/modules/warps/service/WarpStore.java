package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.database.SqlExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * SQLite-backed storage of server warps.
 *
 * <p>Primary key is {@code name} with {@code COLLATE NOCASE} so {@code /warp Spawn} and {@code
 * /warp SPAWN} hit the same row.
 */
public final class WarpStore {

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS warps (
        name TEXT PRIMARY KEY COLLATE NOCASE,
        world TEXT NOT NULL,
        x REAL NOT NULL,
        y REAL NOT NULL,
        z REAL NOT NULL,
        yaw REAL NOT NULL,
        pitch REAL NOT NULL,
        created_at INTEGER NOT NULL,
        created_by_id TEXT NOT NULL
      )
      """;

  private static final String UPSERT =
      """
      INSERT OR REPLACE INTO warps \
      (name, world, x, y, z, yaw, pitch, created_at, created_by_id) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)\
      """;

  private static final String DELETE =
      """
      DELETE FROM warps WHERE name = ?\
      """;

  private static final String SELECT_ONE =
      """
      SELECT name, world, x, y, z, yaw, pitch, created_at, created_by_id \
      FROM warps WHERE name = ?\
      """;

  private static final String SELECT_ALL =
      """
      SELECT name, world, x, y, z, yaw, pitch, created_at, created_by_id \
      FROM warps ORDER BY name\
      """;

  private final SqlExecutor sqlExecutor;

  public WarpStore(SqlExecutor sqlExecutor) {
    this.sqlExecutor = Objects.requireNonNull(sqlExecutor, "sqlExecutor");
    sqlExecutor.ddl(CREATE_TABLE);
  }

  public Optional<Warp> find(String name) {
    Objects.requireNonNull(name, "name");
    var rows = sqlExecutor.query(SELECT_ONE, WarpStore::readRow, name);
    return rows.isEmpty() ? Optional.empty() : Optional.of(rows.getFirst());
  }

  public List<Warp> list() {
    return sqlExecutor.query(SELECT_ALL, WarpStore::readRow);
  }

  public void save(Warp warp) {
    Objects.requireNonNull(warp, "warp");
    sqlExecutor.update(
        UPSERT,
        warp.name(),
        warp.world(),
        warp.x(),
        warp.y(),
        warp.z(),
        warp.yaw(),
        warp.pitch(),
        warp.createdAt(),
        warp.createdBy().toString());
  }

  /** Deletes the warp. Returns {@code true} when a row was removed. */
  public boolean delete(String name) {
    Objects.requireNonNull(name, "name");
    var before = find(name).isPresent();
    sqlExecutor.update(DELETE, name);
    return before;
  }

  private static Warp readRow(ResultSet rs) throws SQLException {
    return new Warp(
        rs.getString("name"),
        rs.getString("world"),
        rs.getDouble("x"),
        rs.getDouble("y"),
        rs.getDouble("z"),
        (float) rs.getDouble("yaw"),
        (float) rs.getDouble("pitch"),
        rs.getLong("created_at"),
        UUID.fromString(rs.getString("created_by_id")));
  }
}
