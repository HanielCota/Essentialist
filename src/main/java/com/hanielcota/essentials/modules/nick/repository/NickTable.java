package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.database.SqlExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NickTable {

  static final String UPSERT =
      """
      INSERT OR REPLACE INTO nicks \
      (player_id, nickname, real_name, created_at) \
      VALUES (?, ?, ?, ?)\
      """;

  static final String DELETE =
      """
      DELETE FROM nicks WHERE player_id = ?\
      """;

  static final String SELECT_ALL =
      """
      SELECT player_id, nickname, real_name \
      FROM nicks\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS nicks (
        player_id TEXT PRIMARY KEY,
        nickname TEXT NOT NULL COLLATE NOCASE UNIQUE,
        real_name TEXT NOT NULL,
        created_at INTEGER NOT NULL
      )
      """;

  public static void install(@NonNull SqlExecutor executor) {
    executor.ddl(CREATE_TABLE);
  }
}
