package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.database.ResultMapper;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

/**
 * SQLite-backed implementation of {@link HomeRepository}.
 *
 * <p>Primary key is {@code (player_id, name)} (case-insensitive on the name column) so calls to
 * {@link #save} act as upsert per player+name pair.
 */
@RequiredArgsConstructor
public final class SqlHomeRepository implements HomeRepository {

  private final SqlExecutor sqlExecutor;

  @Override
  public Optional<Home> find(@NonNull UUID owner, @NonNull String name) {
    var ownerStr = owner.toString();
    var rows = this.sqlExecutor.query(SqlHomeTable.SELECT_ONE, SqlHomeMapper::read, ownerStr, name);

    if (rows.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(rows.getFirst());
  }

  @Override
  public List<Home> list(@NonNull UUID owner) {
    var ownerStr = owner.toString();
    return this.sqlExecutor.query(SqlHomeTable.SELECT_ALL, SqlHomeMapper::read, ownerStr);
  }

  @Override
  public int count(@NonNull UUID owner) {
    var ownerStr = owner.toString();

    ResultMapper<Integer> totalMapper = rs -> rs.getInt("total");
    var counts = this.sqlExecutor.query(SqlHomeTable.COUNT, totalMapper, ownerStr);

    if (counts.isEmpty()) {
      return 0;
    }

    return counts.getFirst();
  }

  @Override
  public void save(@NonNull Home home) {
    var ownerStr = home.owner().toString();
    var materialStr = home.material().name();

    var name = home.name();
    var world = home.world();

    var x = home.x();
    var y = home.y();
    var z = home.z();
    var yaw = home.yaw();
    var pitch = home.pitch();

    var createdAt = home.createdAt();

    this.sqlExecutor.update(
        SqlHomeTable.UPSERT, ownerStr, name, world, x, y, z, yaw, pitch, materialStr, createdAt);
  }

  @Override
  public boolean delete(@NonNull UUID owner, @NonNull String name) {
    var ownerStr = owner.toString();
    var rowsAffected = this.sqlExecutor.updateCount(SqlHomeTable.DELETE, ownerStr, name);

    return rowsAffected > 0;
  }

  @Override
  public boolean rename(@NonNull UUID owner, @NonNull String oldName, @NonNull String newName) {
    var existing = find(owner, oldName);

    if (existing.isEmpty()) {
      return false;
    }

    var collision = find(owner, newName);

    if (collision.isPresent()) {
      return false;
    }

    var ownerStr = owner.toString();
    var rowsAffected =
        this.sqlExecutor.updateCount(SqlHomeTable.RENAME, newName, ownerStr, oldName);

    return rowsAffected > 0;
  }

  @Override
  public boolean updateMaterial(
      @NonNull UUID owner, @NonNull String name, @NonNull Material material) {
    var ownerStr = owner.toString();
    var materialStr = material.name();

    var rowsAffected =
        this.sqlExecutor.updateCount(SqlHomeTable.UPDATE_MATERIAL, materialStr, ownerStr, name);

    return rowsAffected > 0;
  }
}
