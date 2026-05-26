package com.hanielcota.essentials.modules.spawn.repository;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.spawn.domain.SpawnLocation;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Singleton storage of the server spawn.
 *
 * <p>The table has at most one row (enforced by {@code CHECK (singleton = 1)}); {@link #save}
 * upserts the single row by binding {@code singleton = 1} explicitly.
 */
@RequiredArgsConstructor
public final class SpawnRepository implements SpawnStore {

  private static final int SINGLETON_KEY = 1;

  private final @NonNull SqlExecutor sqlExecutor;
  private final @NonNull SpawnTable table;

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
    var rows = this.sqlExecutor.query(SpawnTable.SELECT, SpawnRepository::readRow);
    if (rows.isEmpty()) {
      return Optional.empty();
    }

    var first = rows.getFirst();
    return Optional.of(first);
  }

  /** Overwrites the stored spawn with {@code location}. */
  public void save(@NonNull SpawnLocation location) {
    this.sqlExecutor.update(
        this.table.upsert(),
        SINGLETON_KEY,
        location.world(),
        location.x(),
        location.y(),
        location.z(),
        location.yaw(),
        location.pitch());
  }
}
