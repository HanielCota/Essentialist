package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.warps.repository.SqlWarpLikeRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Warp likes, cached in memory (loaded once at enable). Keyed lower-case by warp name; the like
 * count is the number of distinct players who liked it. One like per player. Writes are async.
 */
@RequiredArgsConstructor
public final class WarpLikes {

  private final SqlWarpLikeRepository repository;
  private final AsyncDatabaseWriter writer;

  private final Map<String, Set<UUID>> likersByWarp = new HashMap<>();

  private static String key(@NonNull String warpName) {
    return warpName.toLowerCase(Locale.ROOT);
  }

  public void loadAll() {
    var rows = this.repository.loadAll();
    for (var row : rows) {
      var likers = this.likersByWarp.computeIfAbsent(key(row.warpName()), warp -> new HashSet<>());
      likers.add(row.playerId());
    }
  }

  public int count(@NonNull String warpName) {
    var likers = this.likersByWarp.get(key(warpName));
    return likers == null ? 0 : likers.size();
  }

  public boolean hasLiked(@NonNull UUID playerId, @NonNull String warpName) {
    var likers = this.likersByWarp.get(key(warpName));
    return likers != null && likers.contains(playerId);
  }

  /** Drops every like of the warp — called when the warp itself is deleted. */
  public void forgetWarp(@NonNull String warpName) {
    this.likersByWarp.remove(key(warpName));
    this.writer.submit("forget warp likes", () -> this.repository.removeByWarp(warpName));
  }

  /** Toggles the like and persists. Returns {@code true} when the warp is now liked. */
  public boolean toggle(@NonNull UUID playerId, @NonNull String warpName) {
    var likers = this.likersByWarp.computeIfAbsent(key(warpName), warp -> new HashSet<>());

    if (likers.remove(playerId)) {
      this.writer.submit("unlike warp", () -> this.repository.remove(playerId, warpName));
      return false;
    }

    likers.add(playerId);
    this.writer.submit("like warp", () -> this.repository.add(playerId, warpName));
    return true;
  }
}
