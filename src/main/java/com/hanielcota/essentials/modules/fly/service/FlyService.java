package com.hanielcota.essentials.modules.fly.service;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class FlyService {

  /**
   * Toggles the player's flight.
   *
   * <p>Creative and Spectator inherently grant flight, so toggling {@code allowFlight} there would
   * desync with the client and the message would be misleading — those modes return {@link
   * Result#UNSUPPORTED} instead.
   */
  public Result toggle(Player player) {
    GameMode mode = player.getGameMode();
    if (mode == GameMode.CREATIVE || mode == GameMode.SPECTATOR) {
      return Result.UNSUPPORTED;
    }

    boolean enabled = !player.getAllowFlight();
    player.setAllowFlight(enabled);
    if (!enabled) {
      player.setFlying(false);
      return Result.DISABLED;
    }
    return Result.ENABLED;
  }

  public enum Result {
    ENABLED,
    DISABLED,
    UNSUPPORTED
  }
}
