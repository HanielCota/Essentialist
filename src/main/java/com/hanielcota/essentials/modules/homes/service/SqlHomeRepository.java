package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.database.SqlExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Material;

/**
 * SQLite-backed implementation of {@link HomeRepository}.
 *
 * <p>Primary key is {@code (player_id, name)} (case-insensitive on the name column) so calls to
 * {@link #save} act as upsert per player+name pair.
 */
public final class SqlHomeRepository implements HomeRepository {

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
        material TEXT NOT NULL DEFAULT 'RED_BED',
        created_at INTEGER NOT NULL,
        PRIMARY KEY (player_id, name)
      )
      """;

  private static final String UPSERT =
      """
      INSERT OR REPLACE INTO homes \
      (player_id, name, world, x, y, z, yaw, pitch, material, created_at) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\
      """;

  private static final String DELETE =
      """
      DELETE FROM homes WHERE player_id = ? AND name = ?\
      """;

  private static final String RENAME =
      """
      UPDATE homes SET name = ? WHERE player_id = ? AND name = ?\
      """;

  private static final String UPDATE_MATERIAL =
      """
      UPDATE homes SET material = ? WHERE player_id = ? AND name = ?\
      """;

  private static final String SELECT_ONE =
      """
      SELECT player_id, name, world, x, y, z, yaw, pitch, material, created_at \
      FROM homes WHERE player_id = ? AND name = ?\
      """;

  private static final String SELECT_ALL =
      """
      SELECT player_id, name, world, x, y, z, yaw, pitch, material, created_at \
      FROM homes WHERE player_id = ? ORDER BY name\
      """;

  private static final String COUNT =
      """
      SELECT COUNT(*) AS total FROM homes WHERE player_id = ?\
      """;

  private static final String HAS_MATERIAL_COLUMN =
      """
      SELECT 1 FROM pragma_table_info('homes') WHERE name = 'material'\
      """;

  private static final String ADD_MATERIAL_COLUMN =
      """
      ALTER TABLE homes ADD COLUMN material TEXT NOT NULL DEFAULT 'RED_BED'\
      """;

  private final SqlExecutor sqlExecutor;

  public SqlHomeRepository(SqlExecutor sqlExecutor) {
    this.sqlExecutor = sqlExecutor;
    sqlExecutor.ddl(CREATE_TABLE);
    migrateMaterialColumn();
  }

  @Override
  public Optional<Home> find(UUID owner, String name) {
    var rows = sqlExecutor.query(SELECT_ONE, SqlHomeRepository::readRow, owner.toString(), name);
    return rows.isEmpty() ? Optional.empty() : Optional.of(rows.getFirst());
  }

  @Override
  public List<Home> list(UUID owner) {
    return sqlExecutor.query(SELECT_ALL, SqlHomeRepository::readRow, owner.toString());
  }

  @Override
  public int count(UUID owner) {
    var counts = sqlExecutor.query(COUNT, rs -> rs.getInt("total"), owner.toString());
    return counts.isEmpty() ? 0 : counts.getFirst();
  }

  @Override
  public void save(Home home) {
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
        home.material().name(),
        home.createdAt());
  }

  @Override
  public boolean delete(UUID owner, String name) {
    if (find(owner, name).isEmpty()) {
      return false;
    }
    sqlExecutor.update(DELETE, owner.toString(), name);
    return true;
  }

  @Override
  public boolean rename(UUID owner, String oldName, String newName) {
    if (find(owner, oldName).isEmpty() || find(owner, newName).isPresent()) {
      return false;
    }
    sqlExecutor.update(RENAME, newName, owner.toString(), oldName);
    return true;
  }

  @Override
  public boolean updateMaterial(UUID owner, String name, Material material) {
    if (find(owner, name).isEmpty()) {
      return false;
    }
    sqlExecutor.update(UPDATE_MATERIAL, material.name(), owner.toString(), name);
    return true;
  }

  // Adds the `material` column to pre-existing databases. New installs already have it via
  // CREATE TABLE; ALTER would error on duplicate column, so we probe pragma_table_info first.
  private void migrateMaterialColumn() {
    var present = sqlExecutor.query(HAS_MATERIAL_COLUMN, rs -> rs.getInt(1));
    if (present.isEmpty()) {
      sqlExecutor.ddl(ADD_MATERIAL_COLUMN);
    }
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
        parseMaterial(rs.getString("material")),
        rs.getLong("created_at"));
  }

  private static Material parseMaterial(String name) {
    if (name == null) return Material.RED_BED;
    var material = Material.matchMaterial(name);
    return material != null ? material : Material.RED_BED;
  }
}
