package com.hanielcota.essentials.modules.kit.repository;

import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

/** {@code kit_uses} table: the last claim timestamp per (player, kit). */
public final class KitUsageTable extends SqlTable {

  static final String SELECT_ALL =
      """
      SELECT kit_id, used_at FROM kit_uses WHERE player_id = ?\
      """;

  static final String DELETE_KIT =
      """
      DELETE FROM kit_uses WHERE kit_id = ?\
      """;

  public KitUsageTable(@NonNull SqlDialect dialect) {
    super(dialect, "kit_uses", CREATE_TABLE, "player_id", "kit_id", "used_at");
  }

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS kit_uses (
        player_id TEXT NOT NULL,
        kit_id TEXT NOT NULL,
        used_at INTEGER NOT NULL,
        PRIMARY KEY (player_id, kit_id)
      )\
      """;
}
