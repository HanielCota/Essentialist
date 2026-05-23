package com.hanielcota.essentials.modules.spawn.service;

import com.hanielcota.essentials.database.SqlExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * SQLite-backed singleton storage of the server spawn.
 *
 * <p>The table has at most one row (enforced by {@code CHECK (singleton = 1)}); {@link #save} uses
 * {@code INSERT OR REPLACE} so the spawn is overwritten in place.
 */
@RequiredArgsConstructor
public final class SpawnStore {

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS spawn (
        singleton INTEGER PRIMARY KEY CHECK (singleton = 1),
        world TEXT NOT NULL,
        x REAL NOT NULL,
        y REAL NOT NULL,
        z REAL NOT NULL,
        yaw REAL NOT NULL,
        pitch REAL NOT NULL
      )
      """;

  private static final String UPSERT =
      """
      INSERT OR REPLACE INTO spawn (singleton, world, x, y, z, yaw, pitch) \
      VALUES (1, ?, ?, ?, ?, ?, ?)\
      """;

  private static final String SELECT =
      """
      SELECT world, x, y, z, yaw, pitch FROM spawn WHERE singleton = 1\
      """;

  private final @NonNull SqlExecutor sqlExecutor;

  public static void install(SqlExecutor sqlExecutor) {
    sqlExecutor.ddl(CREATE_TABLE);
  }

  private static SpawnLocation readRow(ResultSet rs) throws SQLException {
    return new SpawnLocation(
        rs.getString("world"),
        rs.getDouble("x"),
        rs.getDouble("y"),
        rs.getDouble("z"),
        (float) rs.getDouble("yaw"),
        (float) rs.getDouble("pitch"));
  }

  /** Returns the stored spawn, or empty when {@code /setspawn} has not run yet. */
  public Optional<SpawnLocation> load() {
    var rows = sqlExecutor.query(SELECT, SpawnStore::readRow);
    return rows.isEmpty() ? Optional.empty() : Optional.of(rows.getFirst());
  }

  /** Overwrites the stored spawn with {@code location}. */
  public void save(SpawnLocation location) {
    sqlExecutor.update(
        UPSERT,
        location.world(),
        location.x(),
        location.y(),
        location.z(),
        location.yaw(),
        location.pitch());
  }
}
