package com.hanielcota.essentials.modules.tpa.repository.favorite;

import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

public final class TpaFavoriteTable extends SqlTable {

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

  public TpaFavoriteTable(@NonNull SqlDialect dialect) {
    super(
        dialect,
        "tpa_favorites",
        CREATE_TABLE,
        "owner_id",
        "favorite_id",
        "favorite_name",
        "updated_at");
  }
}
