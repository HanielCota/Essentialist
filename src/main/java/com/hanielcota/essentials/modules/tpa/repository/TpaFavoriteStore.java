package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;

public interface TpaFavoriteStore {

  List<TpaFavorite> listAll();

  void save(@NonNull UUID ownerId, @NonNull UUID favoriteId, @NonNull String favoriteName);

  void delete(@NonNull UUID ownerId, @NonNull UUID favoriteId);
}
