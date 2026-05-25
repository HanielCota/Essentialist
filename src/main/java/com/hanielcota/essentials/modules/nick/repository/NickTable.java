package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import lombok.NonNull;

public final class NickTable {

  static final String SELECT_ALL =
      """
      SELECT player_id, nickname, real_name \
      FROM nicks\
      """;

  static final String DELETE =
      """
      DELETE FROM nicks WHERE player_id = ?\
      """;

  private final String upsert;
  private final String createTable;

  public NickTable(@NonNull SqlDialect dialect) {
    this.upsert = dialect.upsertInto("nicks", "player_id", "nickname", "real_name", "created_at");

    var caseInsensitive = dialect.caseInsensitiveTextSuffix();
    this.createTable =
        "CREATE TABLE IF NOT EXISTS nicks (\n"
            + "  player_id TEXT PRIMARY KEY,\n"
            + "  nickname TEXT NOT NULL"
            + caseInsensitive
            + " UNIQUE,\n"
            + "  real_name TEXT NOT NULL,\n"
            + "  created_at INTEGER NOT NULL\n"
            + ")";
  }

  String upsert() {
    return this.upsert;
  }

  public void install(@NonNull SqlExecutor executor) {
    executor.ddl(this.createTable);
  }
}
