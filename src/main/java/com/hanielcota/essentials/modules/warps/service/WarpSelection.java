package com.hanielcota.essentials.modules.warps.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Remembers which warp a player is acting on while the action/occupants submenus are open — the
 * selection has to survive the menu transition (each menu is its own session). Cleared when the
 * player disconnects.
 */
public final class WarpSelection {

  private final Map<UUID, String> byPlayer = new HashMap<>();

  public void select(@NonNull UUID playerId, @NonNull String warpName) {
    this.byPlayer.put(playerId, warpName);
  }

  public @Nullable String of(@NonNull UUID playerId) {
    return this.byPlayer.get(playerId);
  }

  public void clear(@NonNull UUID playerId) {
    this.byPlayer.remove(playerId);
  }
}
