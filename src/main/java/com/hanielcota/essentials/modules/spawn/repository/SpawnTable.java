package com.hanielcota.essentials.modules.spawn.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import lombok.NonNull;

public final class SpawnTable {

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

  /**
   * Upsert binds {@code singleton} as a parameter (always {@code 1}) so the SQL stays a pure
   * dialect-generated INSERT-or-replace template. The table's {@code CHECK (singleton = 1)}
   * constraint enforces the singleton invariant; callers in {@link SpawnRepository} pass {@code 1}
   * for the first parameter unconditionally.
   */
  private final String upsert;

  public SpawnTable(@NonNull SqlDialect dialect) {
    this.upsert = dialect.upsertInto("spawn", "singleton", "world", "x", "y", "z", "yaw", "pitch");
  }

  String upsert() {
    return this.upsert;
  }

  public void install(@NonNull SqlExecutor executor) {
    executor.ddl(CREATE_TABLE);
  }
}
