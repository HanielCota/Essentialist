package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.database.SqlExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TpaHistoryTable {

  static final String INSERT =
      """
      INSERT INTO tpa_history \
      (requester_id, target_id, target_name, type, status, created_at, resolved_at, world, x, y, z) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\
      """;

  static final String TRIM =
      """
      DELETE FROM tpa_history \
      WHERE requester_id = ? AND id NOT IN (
        SELECT id FROM tpa_history \
        WHERE requester_id = ? ORDER BY created_at DESC LIMIT ?
      )
      """;

  static final String LIST =
      """
      SELECT requester_id, target_id, target_name, type, status, created_at, resolved_at, world, x, y, z \
      FROM tpa_history \
      WHERE requester_id = ? ORDER BY created_at DESC LIMIT ?\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS tpa_history (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        requester_id TEXT NOT NULL,
        target_id TEXT NOT NULL,
        target_name TEXT NOT NULL,
        type TEXT NOT NULL,
        status TEXT NOT NULL,
        created_at INTEGER NOT NULL,
        resolved_at INTEGER NOT NULL,
        world TEXT,
        x REAL,
        y REAL,
        z REAL
      )
      """;

  private static final String CREATE_INDEX =
      """
      CREATE INDEX IF NOT EXISTS idx_tpa_history_requester \
      ON tpa_history (requester_id, created_at DESC)\
      """;

  public static void install(@NonNull SqlExecutor executor) {
    executor.ddl(CREATE_TABLE, CREATE_INDEX);
  }
}
