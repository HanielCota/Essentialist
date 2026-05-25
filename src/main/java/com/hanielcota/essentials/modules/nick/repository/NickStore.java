package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.nick.model.NickEntry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * SQLite-backed storage of nickname assignments. The {@code nickname} column uses {@code COLLATE
 * NOCASE} so /realname lookups and uniqueness checks are case-insensitive.
 */
@RequiredArgsConstructor
public final class NickStore {

  private final SqlExecutor sqlExecutor;

  private static NickEntry readRow(@NonNull ResultSet rs) throws SQLException {
    var idStr = rs.getString("player_id");
    var nickname = rs.getString("nickname");
    var realName = rs.getString("real_name");

    var id = UUID.fromString(idStr);

    return new NickEntry(id, nickname, realName);
  }

  public List<NickEntry> list() {
    return this.sqlExecutor.query(NickTable.SELECT_ALL, NickStore::readRow);
  }

  public void save(@NonNull NickEntry entry) {
    var idStr = entry.id().toString();
    var nickname = entry.nickname();
    var realName = entry.realName();
    var createdAt = Instant.now().toEpochMilli();

    this.sqlExecutor.update(NickTable.UPSERT, idStr, nickname, realName, createdAt);
  }

  /** Deletes the entry. Returns {@code true} when a row was removed. */
  public boolean delete(@NonNull UUID id) {
    var idStr = id.toString();
    var affected = this.sqlExecutor.updateCount(NickTable.DELETE, idStr);

    return affected > 0;
  }
}
