package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.warps.repository.SqlWarpFavoriteRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Per-player favorite warps, cached in memory (loaded once at enable) so the menu reads without
 * touching SQL. Warp names are keyed lower-case to match the case-insensitive warp table. Writes go
 * through the async writer.
 */
@RequiredArgsConstructor
public final class WarpFavorites {

  private final SqlWarpFavoriteRepository repository;
  private final AsyncDatabaseWriter writer;

  private final Map<UUID, Set<String>> byPlayer = new HashMap<>();

  private static String key(@NonNull String warpName) {
    return warpName.toLowerCase(Locale.ROOT);
  }

  public void loadAll() {
    var rows = this.repository.loadAll();
    for (var row : rows) {
      var favorites = this.byPlayer.computeIfAbsent(row.playerId(), id -> new HashSet<>());
      favorites.add(key(row.warpName()));
    }
  }

  public boolean isFavorite(@NonNull UUID playerId, @NonNull String warpName) {
    var favorites = this.byPlayer.get(playerId);
    return favorites != null && favorites.contains(key(warpName));
  }

  /** Drops the warp from every player's favorites — called when the warp itself is deleted. */
  public void forgetWarp(@NonNull String warpName) {
    var key = key(warpName);
    for (var favorites : this.byPlayer.values()) {
      favorites.remove(key);
    }

    this.writer.submit("forget warp favorites", () -> this.repository.removeByWarp(warpName));
  }

  /** Toggles the favorite and persists. Returns {@code true} when the warp is now a favorite. */
  public boolean toggle(@NonNull UUID playerId, @NonNull String warpName) {
    var favorites = this.byPlayer.computeIfAbsent(playerId, id -> new HashSet<>());
    var key = key(warpName);

    if (favorites.remove(key)) {
      this.writer.submit("unfavorite warp", () -> this.repository.remove(playerId, warpName));
      return false;
    }

    favorites.add(key);
    this.writer.submit("favorite warp", () -> this.repository.add(playerId, warpName));
    return true;
  }
}
