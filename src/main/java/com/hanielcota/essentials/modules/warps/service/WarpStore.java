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

    return Optional.of(rows.getFirst());
  }

  public List<Warp> list() {
    return this.sqlExecutor.query(WarpTable.SELECT_ALL, WarpStore::readRow);
  }

  public void save(@NonNull Warp warp) {
    var creatorIdStr = warp.createdBy().toString();

    this.sqlExecutor.update(
        WarpTable.UPSERT,
        warp.name(),
        warp.world(),
        warp.x(),
        warp.y(),
        warp.z(),
        warp.yaw(),
        warp.pitch(),
        warp.createdAt(),
        creatorIdStr);
  }

  /** Deletes the warp. Returns {@code true} when a row was removed. */
  public boolean delete(@NonNull String name) {
    var before = find(name).isPresent();
    if (!before) {
      return false;
    }

    this.sqlExecutor.update(WarpTable.DELETE, name);
    return true;
  }
}
