package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

public final class NickTable extends SqlTable {

  static final String SELECT_ALL =
      """
      SELECT player_id, nickname, real_name \
      FROM nicks\
      """;

  static final String DELETE =
      """
      DELETE FROM nicks WHERE player_id = ?\
      """;

  public NickTable(@NonNull SqlDialect dialect) {
    super(
        dialect,
        "nicks",
        buildCreateTable(dialect),
        "player_id",
        "nickname",
        "real_name",
        "created_at");
  }

  private static String buildCreateTable(@NonNull SqlDialect dialect) {
    var caseInsensitive = dialect.caseInsensitiveTextSuffix();
    return "CREATE TABLE IF NOT EXISTS nicks (\n"
        + "  player_id TEXT PRIMARY KEY,\n"
        + "  nickname TEXT NOT NULL"
        + caseInsensitive
        + " UNIQUE,\n"
        + "  real_name TEXT NOT NULL,\n"
        + "  created_at INTEGER NOT NULL\n"
        + ")";
  }
}
