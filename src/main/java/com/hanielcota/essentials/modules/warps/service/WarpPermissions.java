package com.hanielcota.essentials.modules.warps.service;

import java.util.Locale;
import lombok.NonNull;
import org.bukkit.permissions.Permissible;

/**
 * Per-warp access rule: a player may use a warp when they hold {@code essentials.warp.use.<name>}
 * (lowercased) or the {@code essentials.warp.use.*} wildcard. Isolated from {@link WarpService} so
 * the rule has one home and is testable on its own.
 */
public final class WarpPermissions {

  private static final String USE_PREFIX = "essentials.warp.use.";
  private static final String USE_WILDCARD = "essentials.warp.use.*";

  public boolean canUse(@NonNull Permissible permissible, @NonNull String warpName) {
    if (permissible.hasPermission(USE_WILDCARD)) {
      return true;
    }

    var permissionNode = USE_PREFIX + warpName.toLowerCase(Locale.ROOT);
    return permissible.hasPermission(permissionNode);
  }
}
