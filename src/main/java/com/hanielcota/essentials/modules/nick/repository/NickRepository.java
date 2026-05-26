package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Storage of nickname assignments. The {@code nickname} column carries the dialect's
 * case-insensitive collation so {@code /realname} lookups and uniqueness checks ignore case.
 */
@RequiredArgsConstructor
public final class NickRepository implements NickStore {

  private final SqlExecutor sqlExecutor;
  private final NickTable table;

  private static NickEntry readRow(@NonNull ResultSet rs) throws SQLException {
    var idStr = rs.getString("player_id");
    var nickname = rs.getString("nickname");
    var realName = rs.getString("real_name");

    var id = UUID.fromString(idStr);

    return new NickEntry(id, nickname, realName);
  }

  public List<NickEntry> list() {
    return this.sqlExecutor.query(NickTable.SELECT_ALL, NickRepository::readRow);
  }

  public void save(@NonNull NickEntry entry) {
    var idStr = entry.id().toString();
    var nickname = entry.nickname();
    var realName = entry.realName();
    var createdAt = Instant.now().toEpochMilli();

    this.sqlExecutor.update(this.table.upsert(), idStr, nickname, realName, createdAt);
  }

  /** Deletes the entry. Returns {@code true} when a row was removed. */
  public boolean delete(@NonNull UUID id) {
    var idStr = id.toString();
    var affected = this.sqlExecutor.updateCount(NickTable.DELETE, idStr);

    return affected > 0;
  }
}
