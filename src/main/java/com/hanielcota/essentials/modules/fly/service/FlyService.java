package com.hanielcota.essentials.modules.fly.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public final class FlyService {

  private final Set<UUID> enabled = ConcurrentHashMap.newKeySet();

  /**
   * Toggles the player's flight.
   *
   * <p>Creative and Spectator inherently grant flight, so changing {@code allowFlight} there would
   * desync with the client and the message would be misleading — those modes return {@link
   * Result#UNSUPPORTED} instead.
   */
  public Result toggle(@NonNull Player player) {
    return set(player, !isEnabled(player));
  }

  /** Enables or disables flight explicitly. See {@link #toggle} for the Creative/Spectator note. */
  public Result set(@NonNull Player player, boolean enable) {
    var mode = player.getGameMode();
    if (mode == GameMode.CREATIVE || mode == GameMode.SPECTATOR) {
      return Result.UNSUPPORTED;
    }

    if (enable) {
      this.enabled.add(player.getUniqueId());
      player.setAllowFlight(true);
      return Result.ENABLED;
    }
    this.enabled.remove(player.getUniqueId());
    player.setAllowFlight(false);
    player.setFlying(false);
    return Result.DISABLED;
  }

  /** Whether {@code player} has command-managed flight active in this session. */
  public boolean isEnabled(@NonNull Player player) {
    return this.enabled.contains(player.getUniqueId());
  }

  /** Drops the session entry for {@code id} without touching any live player state. */
  public void forget(@NonNull UUID id) {
    this.enabled.remove(id);
  }

  public enum Result {
    ENABLED,
    DISABLED,
    UNSUPPORTED
  }
}
