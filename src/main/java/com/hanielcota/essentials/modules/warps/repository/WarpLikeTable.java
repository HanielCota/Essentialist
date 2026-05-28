package com.hanielcota.essentials.modules.warps.repository;

import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

/** Join table of warp likes: one row per (player, warp). Count = rows for a warp. */
public final class WarpLikeTable extends SqlTable {

  static final String DELETE =
      """
      DELETE FROM warp_likes WHERE player_id = ? AND warp_name = ?\
      """;

  static final String DELETE_BY_WARP =
      """
      DELETE FROM warp_likes WHERE warp_name = ?\
      """;

  static final String SELECT_ALL =
      """
      SELECT player_id, warp_name FROM warp_likes\
      """;

  public WarpLikeTable(@NonNull SqlDialect dialect) {
    super(dialect, "warp_likes", buildCreateTable(dialect), "player_id", "warp_name");
  }

  private static String buildCreateTable(@NonNull SqlDialect dialect) {
    var caseInsensitive = dialect.caseInsensitiveTextSuffix();
    return "CREATE TABLE IF NOT EXISTS warp_likes (\n"
        + "  player_id TEXT NOT NULL,\n"
        + "  warp_name TEXT NOT NULL"
        + caseInsensitive
        + ",\n"
        + "  PRIMARY KEY (player_id, warp_name)\n"
        + ")";
  }
}
