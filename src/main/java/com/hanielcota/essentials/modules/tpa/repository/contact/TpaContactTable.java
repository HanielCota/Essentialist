package com.hanielcota.essentials.modules.tpa.repository.contact;

import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

public final class TpaContactTable extends SqlTable {

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

  public TpaContactTable(@NonNull SqlDialect dialect) {
    super(
        dialect,
        "tpa_contacts",
        CREATE_TABLE,
        "owner_id",
        "target_id",
        "target_name",
        "count",
        "last_used_at");
  }
}
