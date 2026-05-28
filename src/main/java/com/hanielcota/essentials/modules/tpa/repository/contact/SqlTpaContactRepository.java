package com.hanielcota.essentials.modules.tpa.repository.contact;

import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SqlTpaContactRepository implements TpaContactRepository {

  private final SqlExecutor sqlExecutor;
  private final TpaContactTable table;

  private static TpaContact readRow(@NonNull ResultSet rs) throws SQLException {
    var ownerId = UUID.fromString(rs.getString("owner_id"));
    var targetId = UUID.fromString(rs.getString("target_id"));
    var targetName = rs.getString("target_name");
    var count = rs.getLong("count");
    var lastUsedAt = rs.getLong("last_used_at");

    return new TpaContact(ownerId, targetId, targetName, count, lastUsedAt);
  }

  public List<TpaContact> listAll() {
    return this.sqlExecutor.query(TpaContactTable.SELECT_ALL, SqlTpaContactRepository::readRow);
  }

  public void save(@NonNull TpaContact contact) {
    this.sqlExecutor.update(
        this.table.upsert(),
        contact.ownerId().toString(),
        contact.targetId().toString(),
        contact.targetName(),
        contact.count(),
        contact.lastUsedAtEpochMs());
  }
}
