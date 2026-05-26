package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SqlTpaBlockRepository implements TpaBlockRepository {

  private final SqlExecutor sqlExecutor;
  private final TpaBlockTable table;

  private static TpaBlockService.Entry readRow(@NonNull ResultSet rs) throws SQLException {
    var blockerId = UUID.fromString(rs.getString("blocker_id"));
    var blockedId = UUID.fromString(rs.getString("blocked_id"));
    var blockedName = rs.getString("blocked_name");

    return new TpaBlockService.Entry(blockerId, blockedId, blockedName);
  }

  public List<TpaBlockService.Entry> listAll() {
    return this.sqlExecutor.query(TpaBlockTable.SELECT_ALL, SqlTpaBlockRepository::readRow);
  }

  public void save(@NonNull UUID blockerId, @NonNull UUID blockedId, @NonNull String blockedName) {
    var updatedAt = Instant.now().toEpochMilli();

    this.sqlExecutor.update(
        this.table.upsert(), blockerId.toString(), blockedId.toString(), blockedName, updatedAt);
  }

  public void delete(@NonNull UUID blockerId, @NonNull UUID blockedId) {
    this.sqlExecutor.update(TpaBlockTable.DELETE, blockerId.toString(), blockedId.toString());
  }
}
