package com.hanielcota.essentials.modules.mute.service;

import com.hanielcota.essentials.database.SqlExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MuteTable {

  static final String UPSERT =
      """
      INSERT OR REPLACE INTO mutes \
      (player_id, expires_at, created_at) \
      VALUES (?, ?, ?)\
      """;

  static final String DELETE =
      """
      DELETE FROM mutes WHERE player_id = ?\
      """;

  static final String DELETE_EXPIRED =
      """
      DELETE FROM mutes WHERE expires_at IS NOT NULL AND expires_at <= ?\
      """;

  static final String SELECT_ACTIVE =
      """
      SELECT player_id, expires_at, created_at \
      FROM mutes \
      WHERE expires_at IS NULL OR expires_at > ?\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS mutes (
        player_id TEXT PRIMARY KEY,
        expires_at INTEGER,
        created_at INTEGER NOT NULL
      )
      """;

  public static void install(@NonNull SqlExecutor sqlExecutor) {
    sqlExecutor.ddl(CREATE_TABLE);
  }
}
