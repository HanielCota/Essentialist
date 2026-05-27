package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.modules.homes.domain.HomeOrdering;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * In-memory per-player choice of {@link HomeOrdering} for the /homes menu sort button. Resets to
 * {@link HomeOrdering#NAME} on login (no DB persistence, defer to a future homes-profile table if
 * needed). Cleared on quit by {@code HomesSessionCleanupListener}.
 */
public final class HomeOrderingPreferences {

  private final ConcurrentHashMap<UUID, HomeOrdering> orderings = new ConcurrentHashMap<>();

  public HomeOrdering of(@NonNull UUID player) {
    return this.orderings.getOrDefault(player, HomeOrdering.NAME);
  }

  public HomeOrdering cycle(@NonNull UUID player) {
    return this.orderings.compute(
        player, (id, current) -> current == null ? HomeOrdering.NAME.next() : current.next());
  }

  public void clear(@NonNull UUID player) {
    this.orderings.remove(player);
  }
}
