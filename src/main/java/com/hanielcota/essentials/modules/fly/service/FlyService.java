package com.hanielcota.essentials.modules.fly.service;

import org.bukkit.entity.Player;

public final class FlyService {

  public boolean toggle(Player player) {
    boolean enabled = !player.getAllowFlight();
    player.setAllowFlight(enabled);
    if (!enabled) {
      player.setFlying(false);
    }
    return enabled;
  }
}
