package com.hanielcota.essentials.modules.speed.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.speed.config.SpeedConfig;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class SpeedService {

  private final ConfigHandle<SpeedConfig> config;

  // Players whose speed this module changed. Walk/fly speed persists in player NBT, so /speed is
  // kept session-only by restoring it on quit — but only for players we touched, never stomping a
  // speed another plugin set.
  private final Set<UUID> modified = ConcurrentHashMap.newKeySet();

  private static boolean isOutOfRange(@NonNull SpeedConfig snap, int value) {
    return value < snap.minSpeed() || value > snap.maxSpeed();
  }

  // Bukkit's setWalkSpeed/setFlySpeed throw outside [-1, 1]. A config with minSpeed below -maxSpeed
  // would scale past that bound, so clamp defensively before applying.
  private static float clampToBukkitRange(float scaled) {
    return Math.max(-1f, Math.min(1f, scaled));
  }

  public boolean setWalkSpeed(@NonNull Player player, int value) {
    var snap = this.config.value();
    if (isOutOfRange(snap, value)) {
      return false;
    }

    var maxSpeed = snap.maxSpeed();
    if (maxSpeed == 0) {
      return false;
    }

    // Scale by the configured max so the top value maps to Bukkit's 1.0 ceiling regardless of
    // range.
    var scaled = value / (float) maxSpeed;
    var clamped = clampToBukkitRange(scaled);
    player.setWalkSpeed(clamped);
    this.modified.add(player.getUniqueId());
    return true;
  }

  public boolean setFlySpeed(@NonNull Player player, int value) {
    var snap = this.config.value();
    if (isOutOfRange(snap, value)) {
      return false;
    }

    var maxSpeed = snap.maxSpeed();
    if (maxSpeed == 0) {
      return false;
    }

    var scaled = value / (float) maxSpeed;
    var clamped = clampToBukkitRange(scaled);
    player.setFlySpeed(clamped);
    this.modified.add(player.getUniqueId());
    return true;
  }

  /** Restores walk and fly speed to the configured defaults. */
  public void reset(@NonNull Player player) {
    var snap = this.config.value();
    player.setWalkSpeed(snap.resetWalkSpeed());
    player.setFlySpeed(snap.resetFlySpeed());

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
