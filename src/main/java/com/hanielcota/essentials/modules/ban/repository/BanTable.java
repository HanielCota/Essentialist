package com.hanielcota.essentials.modules.ban.repository;

import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

public final class BanTable extends SqlTable {

  static final String DELETE =
      """
      DELETE FROM bans WHERE player_id = ?\
      """;

  static final String DELETE_EXPIRED =
      """
      DELETE FROM bans WHERE expires_at IS NOT NULL AND expires_at <= ?\
      """;

  static final String SELECT_ACTIVE =
      """
      SELECT player_id, name, reason, issuer, expires_at, created_at \
      FROM bans \
      WHERE expires_at IS NULL OR expires_at > ?\
      """;

  static final String SELECT_ACTIVE_BY_ID =
      """
      SELECT player_id, name, reason, issuer, expires_at, created_at \
      FROM bans \
      WHERE player_id = ? AND (expires_at IS NULL OR expires_at > ?)\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS bans (
        player_id TEXT PRIMARY KEY,
        name TEXT NOT NULL,
        reason TEXT NOT NULL,
        issuer TEXT NOT NULL,
        expires_at INTEGER,
        created_at INTEGER NOT NULL
      )
      """;

  public BanTable(@NonNull SqlDialect dialect) {
    super(
        dialect,
        "bans",
        CREATE_TABLE,
        "player_id",
        "name",
        "reason",
        "issuer",
        "expires_at",
        "created_at");
  }
}
