package com.hanielcota.essentials.modules.teleport.history;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import lombok.NonNull;

public final class TeleportHistoryTable {

  static final String INSERT =
      """
      INSERT INTO teleport_history (player_id, world, x, y, z, yaw, pitch, created_at) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)\
      """;

  static final String TRIM =
      """
      DELETE FROM teleport_history
      WHERE player_id = ?
        AND id NOT IN (
          SELECT id FROM teleport_history
          WHERE player_id = ?
          ORDER BY created_at DESC
          LIMIT ?
        )
      """;

  static final String LIST =
      """
      SELECT id, world, x, y, z, yaw, pitch, created_at FROM teleport_history \
      WHERE player_id = ? ORDER BY created_at DESC LIMIT ?\
      """;

  static final String DELETE_BY_ID =
      """
      DELETE FROM teleport_history WHERE id = ? AND player_id = ?\
      """;

  private static final String CREATE_INDEX =
      """
      CREATE INDEX IF NOT EXISTS idx_teleport_history_player \
      ON teleport_history(player_id, created_at)\
      """;

  private final String createTable;

  public TeleportHistoryTable(@NonNull SqlDialect dialect) {
    var pkColumn = dialect.autoIncrementPrimaryKey("id");
    this.createTable =
        "CREATE TABLE IF NOT EXISTS teleport_history (\n"
            + "  "
            + pkColumn
            + ",\n"
            + "  player_id TEXT NOT NULL,\n"
            + "  world TEXT NOT NULL,\n"
            + "  x REAL NOT NULL,\n"
            + "  y REAL NOT NULL,\n"
            + "  z REAL NOT NULL,\n"
            + "  yaw REAL NOT NULL,\n"
            + "  pitch REAL NOT NULL,\n"
            + "  created_at INTEGER NOT NULL\n"
            + ")";
  }

  public void install(@NonNull SqlExecutor executor) {
    executor.ddl(this.createTable, CREATE_INDEX);
  }
}
