package com.hanielcota.essentials.modules.teleport.history;

import com.hanielcota.essentials.database.SqlSchema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS teleport_history (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        player_id TEXT NOT NULL,
        world TEXT NOT NULL,
        x REAL NOT NULL,
        y REAL NOT NULL,
        z REAL NOT NULL,
        yaw REAL NOT NULL,
        pitch REAL NOT NULL,
        created_at INTEGER NOT NULL
      )
      """;

  private static final String CREATE_INDEX =
      """
      CREATE INDEX IF NOT EXISTS idx_teleport_history_player \
      ON teleport_history(player_id, created_at)\
      """;

  public static void install(@NonNull SqlSchema schema) {
    schema.ddl(CREATE_TABLE, CREATE_INDEX);
  }
}
