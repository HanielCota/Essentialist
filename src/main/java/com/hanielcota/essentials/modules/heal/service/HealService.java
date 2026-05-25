package com.hanielcota.essentials.modules.heal.service;

import lombok.NonNull;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

public final class HealService {

  public boolean heal(@NonNull Player player) {
    var maxHealthAttribute = player.getAttribute(Attribute.MAX_HEALTH);
    if (maxHealthAttribute == null) {
      return false;
    }

    double maxHealth = maxHealthAttribute.getValue();
    if (!Double.isFinite(maxHealth) || maxHealth <= 0) {
      return false;
    }
    if (player.getHealth() >= maxHealth) {
      return false;
    }

    player.setHealth(maxHealth);
    return true;
  }

  /**
   * Heals every alive, non-full player in the given roster. Returns the number of players that were
   * actually healed (skips the dead and the already-full).
   */
  public int healAll(@NonNull Iterable<? extends Player> targets) {
    var healed = 0;
    for (var player : targets) {
      if (player.getHealth() <= 0) {
        continue;
      }
      if (heal(player)) {
        healed++;
      }
    }

    return healed;
  }
}
