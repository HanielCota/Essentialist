package com.hanielcota.essentials.modules.warps.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlTable;
import lombok.NonNull;

public final class WarpTable extends SqlTable {

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

  public WarpTable(@NonNull SqlDialect dialect) {
    super(
        dialect,
        "warps",
        buildCreateTable(dialect),
        "name",
        "world",
        "x",
        "y",
        "z",
        "yaw",
        "pitch",
        "created_at",
        "created_by_id");
  }

  private static String buildCreateTable(@NonNull SqlDialect dialect) {
    var caseInsensitive = dialect.caseInsensitiveTextSuffix();
    return "CREATE TABLE IF NOT EXISTS warps (\n"
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
}
