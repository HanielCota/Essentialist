package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.database.SqlSchema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WarpTable {

  static final String UPSERT =
      """
      INSERT OR REPLACE INTO warps \
      (name, world, x, y, z, yaw, pitch, created_at, created_by_id) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)\
      """;

  static final String DELETE =
      """
      DELETE FROM warps WHERE name = ?\
      """;

  static final String SELECT_ONE =
      """
      SELECT name, world, x, y, z, yaw, pitch, created_at, created_by_id \
      FROM warps WHERE name = ?\
      """;

  static final String SELECT_ALL =
      """
      SELECT name, world, x, y, z, yaw, pitch, created_at, created_by_id \
      FROM warps ORDER BY name\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS warps (
        name TEXT PRIMARY KEY COLLATE NOCASE,
        world TEXT NOT NULL,
        x REAL NOT NULL,
        y REAL NOT NULL,
        z REAL NOT NULL,
        yaw REAL NOT NULL,
        pitch REAL NOT NULL,
        created_at INTEGER NOT NULL,
        created_by_id TEXT NOT NULL
      )
      """;

  public static void install(@NonNull SqlSchema schema) {
    schema.ddl(CREATE_TABLE);
  }
}
