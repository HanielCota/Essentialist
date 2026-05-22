package com.hanielcota.essentials.modules.speed.service;

import org.bukkit.entity.Player;

public final class SpeedService {

  private static final int MIN_SPEED = 1;
  private static final int MAX_SPEED = 10;

  public boolean setWalkSpeed(Player player, int value) {
    if (isOutOfRange(value)) {
      return false;
    }

    player.setWalkSpeed(value / 10.0f);
    return true;
  }

  public boolean setFlySpeed(Player player, int value) {
    if (isOutOfRange(value)) {
      return false;
    }

    player.setFlySpeed(value / 10.0f);
    return true;
  }

  private static boolean isOutOfRange(int value) {
    return value < MIN_SPEED || value > MAX_SPEED;
  }
}
