package com.hanielcota.essentials.modules.mute.repository;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.mute.model.Mute;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * SQLite-backed storage of mute entries. {@code expires_at} is nullable — a {@code NULL} row is a
 * permanent mute, otherwise the value is epoch millis.
 */
@RequiredArgsConstructor
public final class MuteStore {

  private final SqlExecutor sqlExecutor;

  private static Map.Entry<UUID, Mute> readRow(@NonNull ResultSet rs) throws SQLException {
    var idStr = rs.getString("player_id");
    var expiresMillis = rs.getLong("expires_at");
    var expiresAtIsNull = rs.wasNull();

    var id = UUID.fromString(idStr);
    var mute = expiresAtIsNull ? Mute.permanent() : Mute.until(Instant.ofEpochMilli(expiresMillis));

    return Map.entry(id, mute);
  }

  /** Active mutes — permanent rows plus timed rows still in the future relative to {@code now}. */
  public List<Map.Entry<UUID, Mute>> listActive(@NonNull Instant now) {
    var cutoff = now.toEpochMilli();

    return this.sqlExecutor.query(MuteTable.SELECT_ACTIVE, MuteStore::readRow, cutoff);
  }

  public void save(@NonNull UUID id, @NonNull Mute mute) {
    var idStr = id.toString();
    var expiresAt = mute.expiresAt();
    var expiresMillis = expiresAt == null ? null : expiresAt.toEpochMilli();
    var createdAt = Instant.now().toEpochMilli();

    this.sqlExecutor.update(MuteTable.UPSERT, idStr, expiresMillis, createdAt);
  }

  /** Deletes the mute. Returns {@code true} when a row was removed. */
  public boolean delete(@NonNull UUID id) {
    var idStr = id.toString();
    var affected = this.sqlExecutor.updateCount(MuteTable.DELETE, idStr);

    return affected > 0;
  }

  /** Batch-evicts every timed mute whose expiry has already passed. */
  public int deleteExpired(@NonNull Instant now) {
    var cutoff = now.toEpochMilli();

    return this.sqlExecutor.updateCount(MuteTable.DELETE_EXPIRED, cutoff);
  }
}
