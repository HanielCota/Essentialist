package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import lombok.NonNull;

public final class TpaBlockTable {

  static final String SELECT_ALL =
      """
      SELECT blocker_id, blocked_id, blocked_name \
      FROM tpa_blocks\
      """;

  static final String DELETE =
      """
      DELETE FROM tpa_blocks WHERE blocker_id = ? AND blocked_id = ?\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS tpa_blocks (
        blocker_id TEXT NOT NULL,
        blocked_id TEXT NOT NULL,
        blocked_name TEXT NOT NULL,
        updated_at INTEGER NOT NULL,
        PRIMARY KEY (blocker_id, blocked_id)
      )
      """;

  private final String upsert;

  public TpaBlockTable(@NonNull SqlDialect dialect) {
    this.upsert =
        dialect.upsertInto("tpa_blocks", "blocker_id", "blocked_id", "blocked_name", "updated_at");
  }

  String upsert() {
    return this.upsert;
  }

  public void install(@NonNull SqlExecutor executor) {
    executor.ddl(CREATE_TABLE);
  }
}
