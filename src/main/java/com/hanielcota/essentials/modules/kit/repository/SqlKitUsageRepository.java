package com.hanielcota.essentials.modules.kit.repository;

import com.hanielcota.essentials.database.executor.ResultMapper;
import com.hanielcota.essentials.database.executor.SqlExecutor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Relational implementation of {@link KitUsageRepository}; upsert replaces on (player, kit). */
@RequiredArgsConstructor
public final class SqlKitUsageRepository implements KitUsageRepository {

  private static final ResultMapper<Row> ROW_MAPPER =
      rs -> new Row(rs.getString("kit_id"), rs.getLong("used_at"));

  private final SqlExecutor sqlExecutor;
  private final KitUsageTable table;

  @Override
  public Map<String, Long> findAll(@NonNull UUID player) {
    var playerStr = player.toString();
    var rows = this.sqlExecutor.query(KitUsageTable.SELECT_ALL, ROW_MAPPER, playerStr);

    var usage = new LinkedHashMap<String, Long>(rows.size());
    for (var row : rows) {
      usage.put(row.kitId(), row.usedAt());
    }

    return usage;
  }

  @Override
  public void upsert(@NonNull UUID player, @NonNull String kitId, long usedAtMs) {
    var playerStr = player.toString();

    this.sqlExecutor.update(this.table.upsert(), playerStr, kitId, usedAtMs);
  }

  @Override
  public void deleteKit(@NonNull String kitId) {
    this.sqlExecutor.updateCount(KitUsageTable.DELETE_KIT, kitId);
  }

  private record Row(String kitId, long usedAt) {}
}
