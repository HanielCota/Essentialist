package com.hanielcota.essentials.modules.homes.menu;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-player record of which home a sub-flow opened from /homes is acting on (delete dialog,
 * material picker, chat-driven rename). The home name is captured when the dispatch click fires, so
 * the sub-flow does not need to look up which home was clicked.
 *
 * <p>Pure POJO state — quit cleanup lives in {@code HomeTeleportListener} per SRP.
 */
public final class HomesActionTarget {

  private final ConcurrentHashMap<UUID, String> targets = new ConcurrentHashMap<>();

  public void set(UUID player, String homeName) {
    targets.put(player, homeName);
  }

  public Optional<String> consume(UUID player) {
    return Optional.ofNullable(targets.remove(player));
  }

  public void clear(UUID player) {
    targets.remove(player);
  }
}
