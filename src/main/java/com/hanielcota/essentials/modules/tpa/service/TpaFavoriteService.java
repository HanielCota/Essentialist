package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import com.hanielcota.essentials.modules.tpa.repository.TpaFavoriteStore;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

public final class TpaFavoriteService {

  private final @Nullable TpaFavoriteStore repository;
  private final AsyncDatabaseWriter writer;
  private final Map<UUID, Map<UUID, String>> favorites = new ConcurrentHashMap<>();

  public TpaFavoriteService(
      @Nullable TpaFavoriteStore repository, @NonNull AsyncDatabaseWriter writer) {
    this.repository = repository;
    this.writer = writer;
  }

  public void loadAll(@NonNull List<TpaFavorite> entries) {
    for (var entry : entries) {
      addLocal(entry.ownerId(), entry.favoriteId(), entry.favoriteName());
    }
  }

  /** Returns {@code true} when the favorite was newly added, {@code false} when already present. */
  public boolean add(
      @NonNull UUID ownerId, @NonNull UUID favoriteId, @NonNull String favoriteName) {
    var ownerFavorites = this.favorites.computeIfAbsent(ownerId, id -> new ConcurrentHashMap<>());
    var prior = ownerFavorites.put(favoriteId, favoriteName);

    save(ownerId, favoriteId, favoriteName);

    return prior == null;
  }

  /** Returns {@code true} when the favorite existed and was removed. */
  public boolean remove(@NonNull UUID ownerId, @NonNull UUID favoriteId) {
    var ownerFavorites = this.favorites.get(ownerId);
    if (ownerFavorites == null) {
      return false;
    }

    var removed = ownerFavorites.remove(favoriteId);
    if (removed == null) {
      return false;
    }

    delete(ownerId, favoriteId);
    return true;
  }

  public boolean isFavorite(@NonNull UUID ownerId, @NonNull UUID favoriteId) {
    var ownerFavorites = this.favorites.get(ownerId);

    return ownerFavorites != null && ownerFavorites.containsKey(favoriteId);
  }

  public List<TpaFavorite> favoritesOf(@NonNull UUID ownerId) {
    var ownerFavorites = this.favorites.getOrDefault(ownerId, Map.of());

    return ownerFavorites.entrySet().stream()
        .map(entry -> new TpaFavorite(ownerId, entry.getKey(), entry.getValue()))
        .sorted(Comparator.comparing(TpaFavorite::favoriteName, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  private void addLocal(
      @NonNull UUID ownerId, @NonNull UUID favoriteId, @NonNull String favoriteName) {
    var ownerFavorites = this.favorites.computeIfAbsent(ownerId, id -> new ConcurrentHashMap<>());
    ownerFavorites.put(favoriteId, favoriteName);
  }

  private void save(@NonNull UUID ownerId, @NonNull UUID favoriteId, @NonNull String favoriteName) {
    if (this.repository == null) {
      return;
    }

    this.writer.submit(
        "save-tpa-favorite", () -> this.repository.save(ownerId, favoriteId, favoriteName));
  }

  private void delete(@NonNull UUID ownerId, @NonNull UUID favoriteId) {
    if (this.repository == null) {
      return;
    }

    this.writer.submit("delete-tpa-favorite", () -> this.repository.delete(ownerId, favoriteId));
  }
}
