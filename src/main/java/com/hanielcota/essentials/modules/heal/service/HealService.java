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
    if (player.getHealth() >= maxHealth) {
      return false;
    }

    player.setHealth(maxHealth);
    return true;
  }
}
