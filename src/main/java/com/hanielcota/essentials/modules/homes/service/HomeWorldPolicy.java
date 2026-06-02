package com.hanielcota.essentials.modules.homes.service;

import java.util.List;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Decides whether a player may create a home in a given world. A world listed in the configured
 * blocked set rejects creation unless the player holds {@code essentials.home.world.bypass}. The
 * blocked set is read live from config so a reload takes effect without rewiring.
 */
@RequiredArgsConstructor
public final class HomeWorldPolicy {

  private static final String BYPASS_PERMISSION = "essentials.home.world.bypass";

  private final Supplier<List<String>> blockedWorlds;

  public boolean isCreationBlocked(@NonNull Player owner, @NonNull String worldName) {
    var blocked = this.blockedWorlds.get();
    if (!blocked.contains(worldName)) {
      return false;
    }

    return !owner.hasPermission(BYPASS_PERMISSION);
  }
}
