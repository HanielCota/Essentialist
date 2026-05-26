package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class SqlTpaFavoriteRepository implements TpaFavoriteRepository {

  private final SqlExecutor sqlExecutor;
  private final TpaFavoriteTable table;

  private static TpaFavorite readRow(@NonNull ResultSet rs) throws SQLException {
    var ownerId = UUID.fromString(rs.getString("owner_id"));
    var favoriteId = UUID.fromString(rs.getString("favorite_id"));
    var favoriteName = rs.getString("favorite_name");

    return new TpaFavorite(ownerId, favoriteId, favoriteName);
  }

  public List<TpaFavorite> listAll() {
    return this.sqlExecutor.query(TpaFavoriteTable.SELECT_ALL, SqlTpaFavoriteRepository::readRow);
  }

  public void save(@NonNull UUID ownerId, @NonNull UUID favoriteId, @NonNull String favoriteName) {
    var updatedAt = Instant.now().toEpochMilli();

    this.sqlExecutor.update(
        this.table.upsert(), ownerId.toString(), favoriteId.toString(), favoriteName, updatedAt);
  }

  public void delete(@NonNull UUID ownerId, @NonNull UUID favoriteId) {
    this.sqlExecutor.update(TpaFavoriteTable.DELETE, ownerId.toString(), favoriteId.toString());
  }
}
