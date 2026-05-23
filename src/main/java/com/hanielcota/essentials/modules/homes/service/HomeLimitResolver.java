package com.hanielcota.essentials.modules.homes.service;

import java.util.Objects;
import org.bukkit.entity.Player;

/**
 * Resolves a player's home limit from their {@code essentials.home.limit.N} permission nodes.
 *
 * <p>The largest {@code N} the player holds wins; the configured {@code defaultLimit} kicks in when
 * the player has no matching node.
 */
public final class HomeLimitResolver {

  private static final String PREFIX = "essentials.home.limit.";

  private final int defaultLimit;

  public HomeLimitResolver(int defaultLimit) {
    this.defaultLimit = Math.max(0, defaultLimit);
  }

  public int resolve(Player player) {
    Objects.requireNonNull(player, "player");
    var best = -1;
    for (var entry : player.getEffectivePermissions()) {
      if (!entry.getValue()) {
        continue;
      }
      var node = entry.getPermission();
      if (!node.startsWith(PREFIX)) {
        continue;
      }
      try {
        var n = Integer.parseInt(node.substring(PREFIX.length()));
        if (n > best) {
          best = n;
        }
      } catch (NumberFormatException _) {
        // ignore malformed suffix
      }
    }
    return best >= 0 ? best : defaultLimit;
  }
}
