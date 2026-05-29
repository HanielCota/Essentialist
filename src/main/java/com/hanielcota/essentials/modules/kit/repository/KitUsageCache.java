package com.hanielcota.essentials.modules.kit.repository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/** In-memory mirror of {@code kit_uses}, one bucket per online player. */
public final class KitUsageCache {

  private final Map<UUID, Map<String, Long>> byPlayer = new ConcurrentHashMap<>();

  public void loadFor(@NonNull UUID player, @NonNull Map<String, Long> usage) {
    this.byPlayer.put(player, new ConcurrentHashMap<>(usage));
  }

  public boolean isLoaded(@NonNull UUID player) {
    return this.byPlayer.containsKey(player);
  }

  public void evictFor(@NonNull UUID player) {
    this.byPlayer.remove(player);
  }

  public Map<String, Long> all(@NonNull UUID player) {
    var bucket = this.byPlayer.get(player);
    if (bucket == null) {
      return Map.of();
    }

    return Map.copyOf(bucket);
  }

  public void put(@NonNull UUID player, @NonNull String kitId, long usedAtMs) {
    var bucket = this.byPlayer.computeIfAbsent(player, ignored -> new ConcurrentHashMap<>());
    bucket.put(kitId, usedAtMs);
  }

  public void removeKit(@NonNull String kitId) {
    for (var bucket : this.byPlayer.values()) {
      bucket.remove(kitId);
    }
  }
}
