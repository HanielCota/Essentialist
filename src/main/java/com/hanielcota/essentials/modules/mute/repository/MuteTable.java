package com.hanielcota.essentials.modules.mute.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import lombok.NonNull;

public final class MuteTable {

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

  private final String upsert;

  public MuteTable(@NonNull SqlDialect dialect) {
    this.upsert = dialect.upsertInto("mutes", "player_id", "expires_at", "created_at");
  }

  String upsert() {
    return this.upsert;
  }

  public void install(@NonNull SqlExecutor executor) {
    executor.ddl(CREATE_TABLE);
  }
}
