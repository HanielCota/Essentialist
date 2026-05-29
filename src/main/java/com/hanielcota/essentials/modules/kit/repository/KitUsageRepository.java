package com.hanielcota.essentials.modules.kit.repository;

import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

/** Last-claim timestamps per kit for a player. Implementations may cache and persist async. */
public interface KitUsageRepository {

  /** Kit id -> last claim epoch millis for {@code player}. */
  Map<String, Long> findAll(@NonNull UUID player);

  /** Records {@code player} claiming {@code kitId} at {@code usedAtMs}. */
  void upsert(@NonNull UUID player, @NonNull String kitId, long usedAtMs);

  /** Forgets every player's usage of {@code kitId} (called when a kit is deleted). */
  void deleteKit(@NonNull String kitId);
}
