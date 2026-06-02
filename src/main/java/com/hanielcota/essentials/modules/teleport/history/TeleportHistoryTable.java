package com.hanielcota.essentials.modules.teleport.history;

import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.database.schema.SqlTable;
import lombok.NonNull;

public final class TeleportHistoryTable extends SqlTable {

  static final String INSERT =
      """
      INSERT INTO teleport_history (player_id, world, x, y, z, yaw, pitch, created_at, cause) \
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)\
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
      SELECT id, world, x, y, z, yaw, pitch, created_at, cause FROM teleport_history \
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

  /**
   * Backfills the {@code cause} column on databases created before history tracked why a location
   * was captured. Existing rows default to {@code TELEPORT}, the original sole behaviour.
   */
  private static final String ADD_CAUSE_COLUMN =
      "ALTER TABLE teleport_history ADD COLUMN cause TEXT NOT NULL DEFAULT 'TELEPORT'";

  private final String columnExistsQuery;

  public TeleportHistoryTable(@NonNull SqlDialect dialect) {
    super(
        dialect,
        "teleport_history",
        buildCreateTable(dialect),
        "player_id",
        "world",
        "x",
        "y",
        "z",
        "yaw",
        "pitch",
        "created_at",
        "cause");
    this.columnExistsQuery = dialect.columnExistsQuery();
  }

  private static String buildCreateTable(@NonNull SqlDialect dialect) {
    var pkColumn = dialect.autoIncrementPrimaryKey("id");
    return "CREATE TABLE IF NOT EXISTS teleport_history (\n"
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
        + "  created_at INTEGER NOT NULL,\n"
        + "  cause TEXT NOT NULL DEFAULT 'TELEPORT'\n"
        + ")";
  }

  @Override
  public void install(@NonNull SqlExecutor executor) {
    super.install(executor);
    migrateCauseColumn(executor);
    executor.ddl(CREATE_INDEX);
  }

  private void migrateCauseColumn(@NonNull SqlExecutor executor) {
    var present =
        executor.query(this.columnExistsQuery, rs -> rs.getInt(1), "teleport_history", "cause");
    if (present.isEmpty()) {
      executor.ddl(ADD_CAUSE_COLUMN);
    }
  }
}
