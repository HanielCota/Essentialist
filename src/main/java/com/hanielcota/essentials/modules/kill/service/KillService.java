package com.hanielcota.essentials.modules.kill.service;

import lombok.NonNull;
import org.bukkit.entity.Player;

public final class KillService {

  public boolean kill(@NonNull Player player) {
    if (player.getHealth() <= 0) {
      return false;
    }

    player.setHealth(0);
    return true;
  }
}
