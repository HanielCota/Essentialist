package com.hanielcota.essentials.modules.light.service;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public final class LightService {

  public boolean toggle(Player player) {
    var current = player.getPotionEffect(PotionEffectType.NIGHT_VISION);
    boolean enabledByCommand = current != null && current.isInfinite();

    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
    if (enabledByCommand) {
      return false;
    }

    player.addPotionEffect(
        new PotionEffect(PotionEffectType.NIGHT_VISION, PotionEffect.INFINITE_DURATION, 0));
    return true;
  }
}
