package com.hanielcota.essentials.modules.warps.repository;

import com.hanielcota.essentials.database.executor.SqlExecutor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SqlWarpLikeRepository {

  private final SqlExecutor executor;
  private final WarpLikeTable table;

  private static Row readRow(@NonNull ResultSet rs) throws SQLException {
    var playerId = UUID.fromString(rs.getString("player_id"));
    var warpName = rs.getString("warp_name");
    return new Row(playerId, warpName);
  }

  public List<Row> loadAll() {
    return this.executor.query(WarpLikeTable.SELECT_ALL, SqlWarpLikeRepository::readRow);
  }

  public void add(@NonNull UUID playerId, @NonNull String warpName) {
    var idStr = playerId.toString();
    this.executor.update(this.table.upsert(), idStr, warpName);
  }

  public void remove(@NonNull UUID playerId, @NonNull String warpName) {
    var idStr = playerId.toString();
    this.executor.update(WarpLikeTable.DELETE, idStr, warpName);
  }

  /** Drops every like of the warp — used when the warp itself is deleted. */
  public void removeByWarp(@NonNull String warpName) {
    this.executor.update(WarpLikeTable.DELETE_BY_WARP, warpName);
  }

  public record Row(@NonNull UUID playerId, @NonNull String warpName) {}
}
