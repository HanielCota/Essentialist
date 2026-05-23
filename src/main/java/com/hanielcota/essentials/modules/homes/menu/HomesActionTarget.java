package com.hanielcota.essentials.modules.homes.menu;

import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Per-player record of which home a sub-flow opened from /homes is acting on (delete dialog,
 * material picker, chat-driven rename). The home name is captured when the dispatch click fires, so
 * the sub-flow does not need to look up which home was clicked.
 *
 * <p>Pure POJO state — quit cleanup lives in {@code HomeTeleportListener} per SRP.
 */
public final class HomesActionTarget {

  private final ConcurrentHashMap<UUID, String> targets = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<UUID, MaterialCategory> categories = new ConcurrentHashMap<>();

  public void set(@NonNull UUID player, @NonNull String homeName) {
    this.targets.put(player, homeName);
  }

  public String consume(@NonNull UUID player) {
    return this.targets.remove(player);
  }

  public void clear(@NonNull UUID player) {
    this.targets.remove(player);
    this.categories.remove(player);
  }

  public void setCategory(@NonNull UUID player, @NonNull MaterialCategory category) {
    this.categories.put(player, category);
  }

  public MaterialCategory consumeCategory(@NonNull UUID player) {
    return this.categories.remove(player);
  }

  public MaterialCategory peekCategory(@NonNull UUID player) {
    return this.categories.get(player);
  }
}
