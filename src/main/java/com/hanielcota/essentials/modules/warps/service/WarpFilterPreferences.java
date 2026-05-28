package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.modules.warps.menu.WarpFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

/**
 * Per-viewer warps-menu filter, kept in memory. Mirrors the homes module's ordering preference: the
 * filter button cycles the value and the menu re-renders. Defaults to {@link WarpFilter#DEFAULT}.
 */
public final class WarpFilterPreferences {

  private final Map<UUID, WarpFilter> byPlayer = new HashMap<>();

  public @NonNull WarpFilter of(@NonNull UUID playerId) {
    return this.byPlayer.getOrDefault(playerId, WarpFilter.DEFAULT);
  }

  public @NonNull WarpFilter cycle(@NonNull UUID playerId) {
    var next = of(playerId).next();
    this.byPlayer.put(playerId, next);
    return next;
  }

  public void clear(@NonNull UUID playerId) {
    this.byPlayer.remove(playerId);
  }
}
