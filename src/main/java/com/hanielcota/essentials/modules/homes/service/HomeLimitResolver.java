package com.hanielcota.essentials.modules.homes.service;

import java.util.function.IntSupplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.permissions.Permissible;

/**
 * Resolves a player's home limit from their {@code essentials.home.limit.N} permission nodes. The
 * largest {@code N} the player holds wins; the configured {@code defaultLimit} kicks in when the
 * player has no matching node.
 */
@RequiredArgsConstructor
public final class HomeLimitResolver {

  private static final String LIMIT_PERMISSION_PREFIX = "essentials.home.limit.";

  private final IntSupplier defaultLimit;

  public int resolve(@NonNull Permissible player) {
    var maxLimit = Integer.MIN_VALUE;

    for (var attachmentInfo : player.getEffectivePermissions()) {
      var permission = attachmentInfo.getPermission();
      if (attachmentInfo.getValue() && permission.startsWith(LIMIT_PERMISSION_PREFIX)) {
        try {
          var prefixLength = LIMIT_PERMISSION_PREFIX.length();
          var limitSuffix = permission.substring(prefixLength);
          var limit = Integer.parseInt(limitSuffix);

          if (limit > maxLimit) {
            maxLimit = limit;
          }
        } catch (NumberFormatException _) {
          // Skip invalid formats
        }
      }
    }

    if (maxLimit != Integer.MIN_VALUE) {
      return maxLimit;
    }

    return defaultLimit();
  }

  private int defaultLimit() {
    var limit = this.defaultLimit.getAsInt();
    return Math.max(0, limit);
  }
}
