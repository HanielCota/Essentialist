package com.hanielcota.essentials.modules.homes.repository;

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
  public List<Home> listAll() {
    return this.sqlExecutor.query(SqlHomeTable.SELECT_ALL_HOMES, SqlHomeMapper::read);
  }

  @Override
  public int count(@NonNull UUID owner) {
    var ownerStr = owner.toString();
    var counts = this.sqlExecutor.query(SqlHomeTable.COUNT, rs -> rs.getInt("total"), ownerStr);

    if (counts.isEmpty()) {
      return 0;
    }

    return counts.getFirst();
  }

  @Override
  public void save(@NonNull Home home) {
    var ownerStr = home.owner().toString();
    var materialStr = home.material().name();

    this.sqlExecutor.update(
        SqlHomeTable.UPSERT,
        ownerStr,
        home.name(),
        home.world(),
        home.x(),
        home.y(),
        home.z(),
        home.yaw(),
        home.pitch(),
        materialStr,
        home.createdAt());
  }

  @Override
  public boolean delete(@NonNull UUID owner, @NonNull String name) {
    var ownerStr = owner.toString();
    var rowsAffected = this.sqlExecutor.updateCount(SqlHomeTable.DELETE, ownerStr, name);

    return rowsAffected > 0;
  }

  @Override
  public boolean rename(@NonNull UUID owner, @NonNull String oldName, @NonNull String newName) {
    if (find(owner, oldName).isEmpty() || find(owner, newName).isPresent()) {
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
