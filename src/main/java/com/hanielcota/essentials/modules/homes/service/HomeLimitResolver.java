package com.hanielcota.essentials.modules.homes.service;

import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

/**
 * Resolves a player's home limit from their {@code essentials.home.limit.N} permission nodes. The
 * largest {@code N} the player holds wins; the configured {@code defaultLimit} kicks in when the
 * player has no matching node.
 */
public final class HomeLimitResolver {

  private static final String LIMIT_PERMISSION_PREFIX = "essentials.home.limit.";
  private final int defaultLimit;

  public HomeLimitResolver(int defaultLimit) {
    this.defaultLimit = Math.max(0, defaultLimit);
  }

  public int resolve(Player player) {
    return player.getEffectivePermissions().stream()
        .filter(PermissionAttachmentInfo::getValue)
        .map(PermissionAttachmentInfo::getPermission)
        .filter(permission -> permission.startsWith(LIMIT_PERMISSION_PREFIX))
        .map(this::extractLimitFromPermission)
        .flatMap(Optional::stream)
        .reduce(Math::max)
        .orElse(defaultLimit);
  }

  private Optional<Integer> extractLimitFromPermission(String permission) {
    try {
      String limitSuffix = permission.substring(LIMIT_PERMISSION_PREFIX.length());
      return Optional.of(Integer.parseInt(limitSuffix));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }
}
