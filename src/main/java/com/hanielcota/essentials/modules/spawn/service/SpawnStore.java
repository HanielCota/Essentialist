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

  private final @NonNull SqlExecutor sqlExecutor;

  private static SpawnLocation readRow(@NonNull ResultSet rs) throws SQLException {
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
    var rows = this.sqlExecutor.query(SpawnTable.SELECT, SpawnStore::readRow);
    return rows.isEmpty() ? Optional.empty() : Optional.of(rows.getFirst());
  }

  /** Overwrites the stored spawn with {@code location}. */
  public void save(@NonNull SpawnLocation location) {
    this.sqlExecutor.update(
        SpawnTable.UPSERT,
        location.world(),
        location.x(),
        location.y(),
        location.z(),
        location.yaw(),
        location.pitch());
  }
}
