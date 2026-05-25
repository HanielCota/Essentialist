package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * SQLite-backed storage of server warps.
 *
 * <p>Primary key is {@code name} with {@code COLLATE NOCASE} so {@code /warp Spawn} and {@code
 * /warp SPAWN} hit the same row.
 */
@RequiredArgsConstructor
public final class WarpStore {

  private final SqlExecutor sqlExecutor;

  private static Warp readRow(@NonNull ResultSet rs) throws SQLException {
    var name = rs.getString("name");
    var world = rs.getString("world");

    var x = rs.getDouble("x");
    var y = rs.getDouble("y");
    var z = rs.getDouble("z");

    var yaw = (float) rs.getDouble("yaw");
    var pitch = (float) rs.getDouble("pitch");

    var createdAt = rs.getLong("created_at");
    var createdById = UUID.fromString(rs.getString("created_by_id"));

    return new Warp(name, world, x, y, z, yaw, pitch, createdAt, createdById);
  }

  public Optional<Warp> find(@NonNull String name) {
    var rows = this.sqlExecutor.query(WarpTable.SELECT_ONE, WarpStore::readRow, name);

    if (rows.isEmpty()) {
      return Optional.empty();
    }

    var first = rows.getFirst();
    return Optional.of(first);
  }

  public List<Warp> list() {
    return this.sqlExecutor.query(WarpTable.SELECT_ALL, WarpStore::readRow);
  }

  public void save(@NonNull Warp warp) {
    var name = warp.name();
    var world = warp.world();

    var x = warp.x();
    var y = warp.y();
    var z = warp.z();

    var yaw = warp.yaw();
    var pitch = warp.pitch();

    var createdAt = warp.createdAt();
    var creatorIdStr = warp.createdBy().toString();

    this.sqlExecutor.update(
        WarpTable.UPSERT, name, world, x, y, z, yaw, pitch, createdAt, creatorIdStr);
  }

  /** Deletes the warp. Returns {@code true} when a row was removed. */
  public boolean delete(@NonNull String name) {
    // One DELETE — the affected-row count tells us whether the warp existed, so concurrent /delwarp
    // calls don't both observe-then-double-delete.
    var affected = this.sqlExecutor.updateCount(WarpTable.DELETE, name);
    return affected > 0;
  }
}
