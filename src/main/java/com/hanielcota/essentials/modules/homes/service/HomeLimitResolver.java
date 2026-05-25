package com.hanielcota.essentials.modules.homes.service;

import java.util.function.IntSupplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;

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
    var permissions = player.getEffectivePermissions();
    var maxLimit = Integer.MIN_VALUE;

    for (var attachmentInfo : permissions) {
      var candidate = parseLimit(attachmentInfo);

      if (candidate > maxLimit) {
        maxLimit = candidate;
      }
    }

    if (maxLimit != Integer.MIN_VALUE) {
      return maxLimit;
    }

    return defaultLimit();
  }

  private static int parseLimit(@NonNull PermissionAttachmentInfo attachmentInfo) {
    if (!attachmentInfo.getValue()) {
      return Integer.MIN_VALUE;
    }

    var permission = attachmentInfo.getPermission();

    if (!permission.startsWith(LIMIT_PERMISSION_PREFIX)) {
      return Integer.MIN_VALUE;
    }

    var prefixLength = LIMIT_PERMISSION_PREFIX.length();
    var limitSuffix = permission.substring(prefixLength);

    try {
      return Integer.parseInt(limitSuffix);
    } catch (NumberFormatException _) {
      return Integer.MIN_VALUE;
    }
  }

  private int defaultLimit() {
    var limit = this.defaultLimit.getAsInt();
    return Math.max(0, limit);
  }
}
