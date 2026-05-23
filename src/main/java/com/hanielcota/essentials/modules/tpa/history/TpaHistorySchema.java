package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.database.SqlExecutor;
import java.util.Objects;

/**
 * Owns the {@code tpa_history} table definition.
 *
 * <p>Sole responsibility: describe the table's shape and create it on first use. Keeping the DDL
 * here leaves {@link SqliteTpaHistory} free to deal only with statements.
 */
final class TpaHistorySchema {

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS tpa_history (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        requester_id TEXT NOT NULL,
        target_id TEXT NOT NULL,
        target_name TEXT NOT NULL,
        type TEXT NOT NULL,
        status TEXT NOT NULL,
        created_at INTEGER NOT NULL,
        resolved_at INTEGER NOT NULL,
        world TEXT,
        x REAL,
        y REAL,
        z REAL
      )
      """;

  private static final String CREATE_INDEX =
      """
      CREATE INDEX IF NOT EXISTS idx_tpa_history_requester \
      ON tpa_history(requester_id, created_at)
      """;

  private TpaHistorySchema() {}

  /** Creates the table and its lookup index if they do not exist yet. */
  static void create(SqlExecutor sqlExecutor) {
    Objects.requireNonNull(sqlExecutor, "sqlExecutor");
    sqlExecutor.ddl(CREATE_TABLE, CREATE_INDEX);
  }
}
