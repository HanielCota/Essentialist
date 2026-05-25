package com.hanielcota.essentials.modules.warps.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import lombok.NonNull;

public final class WarpTable {

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

  private final String upsert;
  private final String createTable;

  public WarpTable(@NonNull SqlDialect dialect) {
    this.upsert =
        dialect.upsertInto(
            "warps", "name", "world", "x", "y", "z", "yaw", "pitch", "created_at", "created_by_id");

    var caseInsensitive = dialect.caseInsensitiveTextSuffix();
    this.createTable =
        "CREATE TABLE IF NOT EXISTS warps (\n"
            + "  name TEXT PRIMARY KEY"
            + caseInsensitive
            + ",\n"
            + "  world TEXT NOT NULL,\n"
            + "  x REAL NOT NULL,\n"
            + "  y REAL NOT NULL,\n"
            + "  z REAL NOT NULL,\n"
            + "  yaw REAL NOT NULL,\n"
            + "  pitch REAL NOT NULL,\n"
            + "  created_at INTEGER NOT NULL,\n"
            + "  created_by_id TEXT NOT NULL\n"
            + ")";
  }

  String upsert() {
    return this.upsert;
  }

  public void install(@NonNull SqlExecutor executor) {
    executor.ddl(this.createTable);
  }
}
