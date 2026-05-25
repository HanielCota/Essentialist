package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import lombok.NonNull;

public final class SqlHomeTable {

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

  static final String SELECT_ONE =
      """
      SELECT player_id, name, world, x, y, z, yaw, pitch, material, created_at \
      FROM homes WHERE player_id = ? AND name = ?\
      """;

  static final String SELECT_ALL =
      """
      SELECT player_id, name, world, x, y, z, yaw, pitch, material, created_at \
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

  private final String upsert;
  private final String createTable;
  private final String hasMaterialColumn;

  public SqlHomeTable(@NonNull SqlDialect dialect) {
    this.upsert =
        dialect.upsertInto(
            "homes",
            "player_id",
            "name",
            "world",
            "x",
            "y",
            "z",
            "yaw",
            "pitch",
            "material",
            "created_at");

    var caseInsensitive = dialect.caseInsensitiveTextSuffix();
    this.createTable =
        "CREATE TABLE IF NOT EXISTS homes (\n"
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
            + "  PRIMARY KEY (player_id, name)\n"
            + ")";

    this.hasMaterialColumn = dialect.columnExistsQuery();
  }

  String upsert() {
    return this.upsert;
  }

  public void install(@NonNull SqlExecutor sqlExecutor) {
    sqlExecutor.ddl(this.createTable);
    migrateMaterialColumn(sqlExecutor);
  }

  private void migrateMaterialColumn(@NonNull SqlExecutor sqlExecutor) {
    var present =
        sqlExecutor.query(this.hasMaterialColumn, rs -> rs.getInt(1), "homes", "material");
    if (present.isEmpty()) {
      sqlExecutor.ddl(ADD_MATERIAL_COLUMN);
    }
  }
}
