package com.hanielcota.essentials.modules.warps.repository;

import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

public final class WarpTable extends SqlTable {

  static final String DELETE =
      """
      DELETE FROM warps WHERE name = ?\
      """;

  static final String SELECT_ONE =
      """
      SELECT name, world, x, y, z, yaw, pitch, created_at, created_by_id, icon \
      FROM warps WHERE name = ?\
      """;

  static final String SELECT_ALL =
      """
      SELECT name, world, x, y, z, yaw, pitch, created_at, created_by_id, icon \
      FROM warps ORDER BY name\
      """;

  private static final String ADD_ICON_COLUMN =
      """
      ALTER TABLE warps ADD COLUMN icon TEXT NOT NULL DEFAULT 'ENDER_PEARL'\
      """;

  private final String columnExistsQuery;

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
        "created_by_id",
        "icon");
    this.columnExistsQuery = dialect.columnExistsQuery();
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
        + "  created_by_id TEXT NOT NULL,\n"
        + "  icon TEXT NOT NULL DEFAULT 'ENDER_PEARL'\n"
        + ")";
  }

  @Override
  public void install(@NonNull SqlExecutor sqlExecutor) {
    super.install(sqlExecutor);
    migrateColumn(sqlExecutor, "icon", ADD_ICON_COLUMN);
  }

  private void migrateColumn(
      @NonNull SqlExecutor sqlExecutor, @NonNull String column, @NonNull String alter) {
    var present = sqlExecutor.query(this.columnExistsQuery, rs -> rs.getInt(1), "warps", column);
    if (present.isEmpty()) {
      sqlExecutor.ddl(alter);
    }
  }
}
