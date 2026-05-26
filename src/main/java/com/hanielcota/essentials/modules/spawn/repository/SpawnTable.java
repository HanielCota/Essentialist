package com.hanielcota.essentials.modules.spawn.repository;

import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

public final class SpawnTable extends SqlTable {

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

  public SpawnTable(@NonNull SqlDialect dialect) {
    super(dialect, "spawn", CREATE_TABLE, "singleton", "world", "x", "y", "z", "yaw", "pitch");
  }
}
