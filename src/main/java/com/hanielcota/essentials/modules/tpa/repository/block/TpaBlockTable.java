package com.hanielcota.essentials.modules.tpa.repository.block;

import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

public final class TpaBlockTable extends SqlTable {

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

  public TpaBlockTable(@NonNull SqlDialect dialect) {
    super(
        dialect,
        "tpa_blocks",
        CREATE_TABLE,
        "blocker_id",
        "blocked_id",
        "blocked_name",
        "updated_at");
  }
}
