package com.hanielcota.essentials.modules.speed.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class SpeedService {

  private static final int MIN_SPEED = 1;
  private static final int MAX_SPEED = 10;
  private static final float DEFAULT_WALK_SPEED = 0.2f;
  private static final float DEFAULT_FLY_SPEED = 0.1f;

  // Players whose speed this module changed. Walk/fly speed persists in player NBT, so /speed is
  // kept session-only by restoring it on quit — but only for players we touched, never stomping a
  // speed another plugin set.
  private final Set<UUID> modified = ConcurrentHashMap.newKeySet();

  private static boolean isOutOfRange(int value) {
    return value < MIN_SPEED || value > MAX_SPEED;
  }

  public boolean setWalkSpeed(@NonNull Player player, int value) {
    if (isOutOfRange(value)) {
      return false;
    }

    player.setWalkSpeed(value / 10.0f);
    this.modified.add(player.getUniqueId());
    return true;
  }

  public boolean setFlySpeed(@NonNull Player player, int value) {
    if (isOutOfRange(value)) {
      return false;
    }

    player.setFlySpeed(value / 10.0f);
    this.modified.add(player.getUniqueId());
    return true;
  }

  /** Restores walk and fly speed to the Minecraft defaults. */
  public void reset(@NonNull Player player) {
    player.setWalkSpeed(DEFAULT_WALK_SPEED);
    player.setFlySpeed(DEFAULT_FLY_SPEED);

    var id = player.getUniqueId();
    this.modified.remove(id);
  }

  /** Resets on quit only when this module changed the player's speed, so other plugins are safe. */
  public void resetIfModified(@NonNull Player player) {
    var id = player.getUniqueId();
    if (!this.modified.contains(id)) {
      return;
    }

    reset(player);
  }
}
