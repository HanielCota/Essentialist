package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

public final class SqlHomeTable extends SqlTable {

  static final String DELETE =
      """
      DELETE FROM homes WHERE player_id = ? AND name = ?\
      """;

  static final String RENAME =
      """
      UPDATE homes SET name = ? WHERE player_id = ? AND name = ?\
      """;

  static final String UPDATE_MATERIAL =
      """
      UPDATE homes SET material = ? WHERE player_id = ? AND name = ?\
      """;

  static final String UPDATE_PINNED =
      """
      UPDATE homes SET pinned = ? WHERE player_id = ? AND name = ?\
      """;

  static final String SELECT_ONE =
      """
      SELECT player_id, name, world, x, y, z, yaw, pitch, material, created_at, pinned \
      FROM homes WHERE player_id = ? AND name = ?\
      """;

  static final String SELECT_ALL =
      """
      SELECT player_id, name, world, x, y, z, yaw, pitch, material, created_at, pinned \
      FROM homes WHERE player_id = ? ORDER BY name\
      """;

  static final String COUNT =
      """
      SELECT COUNT(*) AS total FROM homes WHERE player_id = ?\
      """;

  private static final String ADD_MATERIAL_COLUMN =
      """
      ALTER TABLE homes ADD COLUMN material TEXT NOT NULL DEFAULT 'RED_BED'\
      """;

  private static final String ADD_PINNED_COLUMN =
      """
      ALTER TABLE homes ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0\
      """;

  private final String columnExistsQuery;

  public SqlHomeTable(@NonNull SqlDialect dialect) {
    super(
        dialect,
        "homes",
        buildCreateTable(dialect),
        "player_id",
        "name",
        "world",
        "x",
        "y",
        "z",
        "yaw",
        "pitch",
        "material",
        "created_at",
        "pinned");
    this.columnExistsQuery = dialect.columnExistsQuery();
  }

  private static String buildCreateTable(@NonNull SqlDialect dialect) {
    var caseInsensitive = dialect.caseInsensitiveTextSuffix();
    return "CREATE TABLE IF NOT EXISTS homes (\n"
        + "  player_id TEXT NOT NULL,\n"
        + "  name TEXT NOT NULL"
        + caseInsensitive
        + ",\n"
        + "  world TEXT NOT NULL,\n"
        + "  x REAL NOT NULL,\n"
        + "  y REAL NOT NULL,\n"
        + "  z REAL NOT NULL,\n"
        + "  yaw REAL NOT NULL,\n"
        + "  pitch REAL NOT NULL,\n"
        + "  material TEXT NOT NULL DEFAULT 'RED_BED',\n"
        + "  created_at INTEGER NOT NULL,\n"
        + "  pinned INTEGER NOT NULL DEFAULT 0,\n"
        + "  PRIMARY KEY (player_id, name)\n"
        + ")";
  }

  @Override
  public void install(@NonNull SqlExecutor sqlExecutor) {
    super.install(sqlExecutor);
    migrateColumn(sqlExecutor, "material", ADD_MATERIAL_COLUMN);
    migrateColumn(sqlExecutor, "pinned", ADD_PINNED_COLUMN);
  }

  private void migrateColumn(
      @NonNull SqlExecutor sqlExecutor, @NonNull String column, @NonNull String alter) {
    var present = sqlExecutor.query(this.columnExistsQuery, rs -> rs.getInt(1), "homes", column);
    if (present.isEmpty()) {
      sqlExecutor.ddl(alter);
    }
  }
}
