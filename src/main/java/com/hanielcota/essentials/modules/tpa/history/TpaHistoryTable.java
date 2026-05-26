package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.database.SqlTable;
import lombok.NonNull;

public final class TpaHistoryTable extends SqlTable {

  static final String INSERT =
      """
      INSERT INTO tpa_history \
      (requester_id, target_id, target_name, type, status, created_at, resolved_at, world, x, y, z) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\
      """;

  static final String TRIM =
      """
      DELETE FROM tpa_history \
      WHERE requester_id = ? AND id NOT IN (
        SELECT id FROM tpa_history \
        WHERE requester_id = ? ORDER BY created_at DESC LIMIT ?
      )
      """;

  static final String LIST =
      """
      SELECT requester_id, target_id, target_name, type, status, created_at, resolved_at, world, x, y, z \
      FROM tpa_history \
      WHERE requester_id = ? ORDER BY created_at DESC LIMIT ?\
      """;

  private static final String CREATE_INDEX =
      """
      CREATE INDEX IF NOT EXISTS idx_tpa_history_requester \
      ON tpa_history (requester_id, created_at DESC)\
      """;

  public TpaHistoryTable(@NonNull SqlDialect dialect) {
    super(
        dialect,
        "tpa_history",
        buildCreateTable(dialect),
        "requester_id",
        "target_id",
        "target_name",
        "type",
        "status",
        "created_at",
        "resolved_at",
        "world",
        "x",
        "y",
        "z");
  }

  private static String buildCreateTable(@NonNull SqlDialect dialect) {
    var pkColumn = dialect.autoIncrementPrimaryKey("id");
    return "CREATE TABLE IF NOT EXISTS tpa_history (\n"
        + "  "
        + pkColumn
        + ",\n"
        + "  requester_id TEXT NOT NULL,\n"
        + "  target_id TEXT NOT NULL,\n"
        + "  target_name TEXT NOT NULL,\n"
        + "  type TEXT NOT NULL,\n"
        + "  status TEXT NOT NULL,\n"
        + "  created_at INTEGER NOT NULL,\n"
        + "  resolved_at INTEGER NOT NULL,\n"
        + "  world TEXT,\n"
        + "  x REAL,\n"
        + "  y REAL,\n"
        + "  z REAL\n"
        + ")";
  }

  @Override
  public void install(@NonNull SqlExecutor executor) {
    super.install(executor);
    executor.ddl(CREATE_INDEX);
  }
}
