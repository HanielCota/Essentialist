package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import lombok.NonNull;

public final class TpaProfileTable {

  static final String SELECT_ALL =
      """
      SELECT player_id, receive_tpa, receive_tpahere, sent_requests, received_requests \
      FROM tpa_profiles\
      """;

  private static final String ADD_RECEIVED_REQUESTS_COLUMN =
      """
      ALTER TABLE tpa_profiles ADD COLUMN received_requests INTEGER NOT NULL DEFAULT 0\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS tpa_profiles (
        player_id TEXT PRIMARY KEY,
        receive_tpa INTEGER NOT NULL,
        receive_tpahere INTEGER NOT NULL,
        sent_requests INTEGER NOT NULL,
        received_requests INTEGER NOT NULL DEFAULT 0,
        updated_at INTEGER NOT NULL
      )
      """;

  private final String upsert;
  private final String hasReceivedRequestsColumn;

  public TpaProfileTable(@NonNull SqlDialect dialect) {
    this.upsert =
        dialect.upsertInto(
            "tpa_profiles",
            "player_id",
            "receive_tpa",
            "receive_tpahere",
            "sent_requests",
            "received_requests",
            "updated_at");
    this.hasReceivedRequestsColumn = dialect.columnExistsQuery();
  }

  String upsert() {
    return this.upsert;
  }

  public void install(@NonNull SqlExecutor executor) {
    executor.ddl(CREATE_TABLE);
    migrateReceivedRequestsColumn(executor);
  }

  private void migrateReceivedRequestsColumn(@NonNull SqlExecutor executor) {
    var present =
        executor.query(
            this.hasReceivedRequestsColumn,
            rs -> rs.getInt(1),
            "tpa_profiles",
            "received_requests");
    if (present.isEmpty()) {
      executor.ddl(ADD_RECEIVED_REQUESTS_COLUMN);
    }
  }
}
