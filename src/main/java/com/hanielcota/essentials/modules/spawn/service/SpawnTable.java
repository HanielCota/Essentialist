package com.hanielcota.essentials.modules.spawn.service;

import com.hanielcota.essentials.database.SqlExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpawnTable {

  static final String UPSERT =
      """
      INSERT OR REPLACE INTO spawn (singleton, world, x, y, z, yaw, pitch) \
      VALUES (1, ?, ?, ?, ?, ?, ?)\
      """;

  static final String SELECT =
      """
      SELECT world, x, y, z, yaw, pitch FROM spawn WHERE singleton = 1\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS spawn (
        singleton INTEGER PRIMARY KEY CHECK (singleton = 1),
        world TEXT NOT NULL,
        x REAL NOT NULL,
        y REAL NOT NULL,
        z REAL NOT NULL,
        yaw REAL NOT NULL,
        pitch REAL NOT NULL
      )
      """;

  public static void install(@NonNull SqlExecutor executor) {
    executor.ddl(CREATE_TABLE);
  }
}
