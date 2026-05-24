package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.database.SqlExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SqlHomeTable {

  static final String UPSERT =
      """
      INSERT OR REPLACE INTO homes \
      (player_id, name, world, x, y, z, yaw, pitch, material, created_at) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\
      """;

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

  private static final String HAS_MATERIAL_COLUMN =
      """
      SELECT 1 FROM pragma_table_info('homes') WHERE name = 'material'\
      """;

  private static final String ADD_MATERIAL_COLUMN =
      """
      ALTER TABLE homes ADD COLUMN material TEXT NOT NULL DEFAULT 'RED_BED'\
      """;

  public static void install(@NonNull SqlExecutor sqlExecutor) {
    sqlExecutor.ddl(CREATE_TABLE);
    migrateMaterialColumn(sqlExecutor);
  }

  private static void migrateMaterialColumn(@NonNull SqlExecutor sqlExecutor) {
    var present = sqlExecutor.query(HAS_MATERIAL_COLUMN, rs -> rs.getInt(1));
    if (present.isEmpty()) {
      sqlExecutor.ddl(ADD_MATERIAL_COLUMN);
    }
  }
}
