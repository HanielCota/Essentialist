package com.hanielcota.essentials.modules.ban.repository;

import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.modules.ban.domain.ActiveBan;
import com.hanielcota.essentials.modules.ban.domain.Ban;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Storage of ban entries. {@code expires_at} is nullable — a {@code NULL} row is a permanent ban,
 * otherwise the value is epoch millis.
 */
@RequiredArgsConstructor
public final class SqlBanRepository implements BanRepository {

  private final SqlExecutor sqlExecutor;
  private final BanTable table;

  private static ActiveBan readRow(@NonNull ResultSet rs) throws SQLException {
    var idStr = rs.getString("player_id");
    var name = rs.getString("name");
    var reason = rs.getString("reason");
    var issuer = rs.getString("issuer");
    var createdMillis = rs.getLong("created_at");
    var expiresMillis = rs.getLong("expires_at");
    var expiresAtIsNull = rs.wasNull();

    var id = UUID.fromString(idStr);
    var createdAt = Instant.ofEpochMilli(createdMillis);
    var expiresAt = expiresAtIsNull ? null : Instant.ofEpochMilli(expiresMillis);
    var ban = new Ban(expiresAt, reason, issuer, createdAt);

    return new ActiveBan(id, name, ban);
  }

  @Override
  public List<ActiveBan> listActive(@NonNull Instant now) {
    var cutoff = now.toEpochMilli();

    return this.sqlExecutor.query(BanTable.SELECT_ACTIVE, SqlBanRepository::readRow, cutoff);
  }

  @Override
  public Optional<Ban> findActive(@NonNull UUID id, @NonNull Instant now) {
    var cutoff = now.toEpochMilli();
    var idStr = id.toString();
    var rows =
        this.sqlExecutor.query(
            BanTable.SELECT_ACTIVE_BY_ID, SqlBanRepository::readRow, idStr, cutoff);

    if (rows.isEmpty()) {
      return Optional.empty();
    }

    var first = rows.get(0);

    return Optional.of(first.ban());
  }

  @Override
  public void save(@NonNull ActiveBan entry) {
    var id = entry.id();
    var ban = entry.ban();
    var idStr = id.toString();
    var name = entry.name();
    var reason = ban.reason();
    var issuer = ban.issuer();
    var expiresAt = ban.expiresAt();
    var expiresMillis = expiresAt == null ? null : expiresAt.toEpochMilli();
    var createdMillis = ban.createdAt().toEpochMilli();

    this.sqlExecutor.update(
        this.table.upsert(), idStr, name, reason, issuer, expiresMillis, createdMillis);
  }

  @Override
  public boolean delete(@NonNull UUID id) {
    var idStr = id.toString();
    var affected = this.sqlExecutor.updateCount(BanTable.DELETE, idStr);

    return affected > 0;
  }

  @Override
  public int deleteExpired(@NonNull Instant now) {
    var cutoff = now.toEpochMilli();

    return this.sqlExecutor.updateCount(BanTable.DELETE_EXPIRED, cutoff);
  }
}
