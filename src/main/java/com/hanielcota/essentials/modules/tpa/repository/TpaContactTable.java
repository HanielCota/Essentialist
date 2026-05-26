package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import lombok.NonNull;

public final class TpaContactTable {

  static final String SELECT_ALL =
      """
      SELECT owner_id, target_id, target_name, count, last_used_at \
      FROM tpa_contacts\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS tpa_contacts (
        owner_id TEXT NOT NULL,
        target_id TEXT NOT NULL,
        target_name TEXT NOT NULL,
        count INTEGER NOT NULL,
        last_used_at INTEGER NOT NULL,
        PRIMARY KEY (owner_id, target_id)
      )
      """;

  private final String upsert;

  public TpaContactTable(@NonNull SqlDialect dialect) {
    this.upsert =
        dialect.upsertInto(
            "tpa_contacts", "owner_id", "target_id", "target_name", "count", "last_used_at");
  }

  String upsert() {
    return this.upsert;
  }

  public void install(@NonNull SqlExecutor executor) {
    executor.ddl(CREATE_TABLE);
  }
}
