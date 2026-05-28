package com.hanielcota.essentials.modules.warps.repository;

import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

/** Join table of player favorites: one row per (player, warp). */
public final class WarpFavoriteTable extends SqlTable {

  static final String DELETE =
      """
      DELETE FROM warp_favorites WHERE player_id = ? AND warp_name = ?\
      """;

  static final String DELETE_BY_WARP =
      """
      DELETE FROM warp_favorites WHERE warp_name = ?\
      """;

  static final String SELECT_ALL =
      """
      SELECT player_id, warp_name FROM warp_favorites\
      """;

  public WarpFavoriteTable(@NonNull SqlDialect dialect) {
    super(dialect, "warp_favorites", buildCreateTable(dialect), "player_id", "warp_name");
  }

  private static String buildCreateTable(@NonNull SqlDialect dialect) {
    var caseInsensitive = dialect.caseInsensitiveTextSuffix();
    return "CREATE TABLE IF NOT EXISTS warp_favorites (\n"
        + "  player_id TEXT NOT NULL,\n"
        + "  warp_name TEXT NOT NULL"
        + caseInsensitive
        + ",\n"
        + "  PRIMARY KEY (player_id, warp_name)\n"
        + ")";
  }
}
