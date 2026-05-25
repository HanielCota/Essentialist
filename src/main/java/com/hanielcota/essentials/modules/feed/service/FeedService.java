package com.hanielcota.essentials.modules.feed.service;

import lombok.NonNull;
import org.bukkit.entity.Player;

public final class FeedService {

  private static final int MAX_FOOD = 20;
  private static final float MAX_SATURATION = 20f;

  public boolean feed(@NonNull Player player) {
    var food = player.getFoodLevel();
    var saturation = player.getSaturation();
    var exhaustion = player.getExhaustion();

    var alreadyFull = food >= MAX_FOOD && saturation >= MAX_SATURATION && exhaustion <= 0f;
    if (alreadyFull) {
      return false;
    }

    player.setFoodLevel(MAX_FOOD);
    player.setSaturation(MAX_SATURATION);
    player.setExhaustion(0f);
    return true;
  }
}
