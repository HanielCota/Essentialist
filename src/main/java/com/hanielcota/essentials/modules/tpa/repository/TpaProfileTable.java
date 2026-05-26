package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.database.SqlTable;
import java.util.List;
import lombok.NonNull;

public final class TpaProfileTable extends SqlTable {

  static final String SELECT_ALL =
      """
      SELECT player_id, receive_tpa, receive_tpahere, sent_requests, received_requests, \
      accepted_sent, accept_count, total_accept_latency_ms, \
      auto_accept_favorites, sounds_enabled, allow_cross_world, notify_when_favorited, \
      dnd_until_ms, favorite_ordering \
      FROM tpa_profiles\
      """;

  private static final String CREATE_TABLE =
      """
      CREATE TABLE IF NOT EXISTS tpa_profiles (
        player_id TEXT PRIMARY KEY,
        receive_tpa INTEGER NOT NULL,
        receive_tpahere INTEGER NOT NULL,
        sent_requests INTEGER NOT NULL,
        received_requests INTEGER NOT NULL DEFAULT 0,
        accepted_sent INTEGER NOT NULL DEFAULT 0,
        accept_count INTEGER NOT NULL DEFAULT 0,
        total_accept_latency_ms INTEGER NOT NULL DEFAULT 0,
        auto_accept_favorites INTEGER NOT NULL DEFAULT 0,
        sounds_enabled INTEGER NOT NULL DEFAULT 1,
        allow_cross_world INTEGER NOT NULL DEFAULT 1,
        notify_when_favorited INTEGER NOT NULL DEFAULT 1,
        dnd_until_ms INTEGER NOT NULL DEFAULT 0,
        favorite_ordering TEXT NOT NULL DEFAULT 'NAME',
        updated_at INTEGER NOT NULL
      )
      """;

  private static final List<Migration> MIGRATIONS =
      List.of(
          new Migration(
              "received_requests",
              "ALTER TABLE tpa_profiles ADD COLUMN received_requests INTEGER NOT NULL DEFAULT 0"),
          new Migration(
              "accepted_sent",
              "ALTER TABLE tpa_profiles ADD COLUMN accepted_sent INTEGER NOT NULL DEFAULT 0"),
          new Migration(
              "accept_count",
              "ALTER TABLE tpa_profiles ADD COLUMN accept_count INTEGER NOT NULL DEFAULT 0"),
          new Migration(
              "total_accept_latency_ms",
              "ALTER TABLE tpa_profiles ADD COLUMN total_accept_latency_ms INTEGER NOT NULL"
                  + " DEFAULT 0"),
          new Migration(
              "auto_accept_favorites",
              "ALTER TABLE tpa_profiles ADD COLUMN auto_accept_favorites INTEGER NOT NULL"
                  + " DEFAULT 0"),
          new Migration(
              "sounds_enabled",
              "ALTER TABLE tpa_profiles ADD COLUMN sounds_enabled INTEGER NOT NULL DEFAULT 1"),
          new Migration(
              "allow_cross_world",
              "ALTER TABLE tpa_profiles ADD COLUMN allow_cross_world INTEGER NOT NULL DEFAULT 1"),
          new Migration(
              "notify_when_favorited",
              "ALTER TABLE tpa_profiles ADD COLUMN notify_when_favorited INTEGER NOT NULL"
                  + " DEFAULT 1"),
          new Migration(
              "dnd_until_ms",
              "ALTER TABLE tpa_profiles ADD COLUMN dnd_until_ms INTEGER NOT NULL DEFAULT 0"),
          new Migration(
              "favorite_ordering",
              "ALTER TABLE tpa_profiles ADD COLUMN favorite_ordering TEXT NOT NULL"
                  + " DEFAULT 'NAME'"));

  private final String columnExistsQuery;

  public TpaProfileTable(@NonNull SqlDialect dialect) {
    super(
        dialect,
        "tpa_profiles",
        CREATE_TABLE,
        "player_id",
        "receive_tpa",
        "receive_tpahere",
        "sent_requests",
        "received_requests",
        "accepted_sent",
        "accept_count",
        "total_accept_latency_ms",
        "auto_accept_favorites",
        "sounds_enabled",
        "allow_cross_world",
        "notify_when_favorited",
        "dnd_until_ms",
        "favorite_ordering",
        "updated_at");
    this.columnExistsQuery = dialect.columnExistsQuery();
  }

  @Override
  public void install(@NonNull SqlExecutor executor) {
    super.install(executor);
    for (var migration : MIGRATIONS) {
      runMigration(executor, migration);
    }
  }

  private void runMigration(@NonNull SqlExecutor executor, @NonNull Migration migration) {
    var present =
        executor.query(
            this.columnExistsQuery, rs -> rs.getInt(1), "tpa_profiles", migration.column());
    if (present.isEmpty()) {
      executor.ddl(migration.alter());
    }
  }

  private record Migration(@NonNull String column, @NonNull String alter) {}
}
