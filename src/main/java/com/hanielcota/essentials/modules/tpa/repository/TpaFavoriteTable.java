package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import lombok.NonNull;

public final class TpaFavoriteTable {

  static final String SELECT_ALL =
      """
      SELECT owner_id, favorite_id, favorite_name \
      FROM tpa_favorites\
      """;

  static final String DELETE =
      """
      DELETE FROM tpa_favorites WHERE owner_id = ? AND favorite_id = ?\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS tpa_favorites (
        owner_id TEXT NOT NULL,
        favorite_id TEXT NOT NULL,
        favorite_name TEXT NOT NULL,
        updated_at INTEGER NOT NULL,
        PRIMARY KEY (owner_id, favorite_id)
      )
      """;

  private final String upsert;

  public TpaFavoriteTable(@NonNull SqlDialect dialect) {
    this.upsert =
        dialect.upsertInto(
            "tpa_favorites", "owner_id", "favorite_id", "favorite_name", "updated_at");
  }

  String upsert() {
    return this.upsert;
  }

  public void install(@NonNull SqlExecutor executor) {
    executor.ddl(CREATE_TABLE);
  }
}
