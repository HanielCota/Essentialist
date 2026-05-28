package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Storage of nickname assignments. The {@code nickname} column carries the dialect's
 * case-insensitive collation so {@code /realname} lookups and uniqueness checks ignore case.
 */
@RequiredArgsConstructor
public final class SqlNickRepository implements NickRepository {

  private final SqlExecutor sqlExecutor;
  private final NickTable table;

  private static NickEntry readRow(@NonNull ResultSet rs) throws SQLException {
    var idStr = rs.getString("player_id");
    var nickname = rs.getString("nickname");
    var realName = rs.getString("real_name");

    var id = UUID.fromString(idStr);

    return new NickEntry(id, nickname, realName);
  }

  @Override
  public List<NickEntry> list() {
    return this.sqlExecutor.query(NickTable.SELECT_ALL, SqlNickRepository::readRow);
  }

  @Override
  public Optional<NickEntry> findById(@NonNull UUID id) {
    var idStr = id.toString();
    var rows =
        this.sqlExecutor.query(NickTable.SELECT_BY_ID, SqlNickRepository::readRow, idStr);

    if (rows.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(rows.get(0));
  }

  @Override
  public Optional<UUID> idByNickname(@NonNull String nickname) {
    var rows =
        this.sqlExecutor.query(
            NickTable.SELECT_BY_NICKNAME,
            rs -> UUID.fromString(rs.getString("player_id")),
            nickname);

    if (rows.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(rows.get(0));
  }

  @Override
  public boolean isTakenByOther(@NonNull String nickname, @NonNull UUID self) {
    var owner = idByNickname(nickname);
    if (owner.isEmpty()) {
      return false;
    }
    return !owner.get().equals(self);
  }

  @Override
  public void save(@NonNull NickEntry entry) {
    var idStr = entry.id().toString();
    var nickname = entry.nickname();
    var realName = entry.realName();
    var createdAt = Instant.now().toEpochMilli();

    this.sqlExecutor.update(this.table.upsert(), idStr, nickname, realName, createdAt);
  }

  @Override
  public boolean delete(@NonNull UUID id) {
    var idStr = id.toString();
    var affected = this.sqlExecutor.updateCount(NickTable.DELETE, idStr);

    return affected > 0;
  }
}
