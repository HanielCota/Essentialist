package com.hanielcota.essentials.modules.warps.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

/**
 * Application service for the warps use cases.
 *
 * <p>Sole responsibility: delegate persistence to {@link WarpStore} and apply the per-warp
 * permission check {@code essentials.warp.use.<name>} (lowercased) — or the bypass node {@code
 * essentials.warp.use.*}.
 */
@RequiredArgsConstructor
public final class WarpService {

  private static final String USE_PREFIX = "essentials.warp.use.";
  private static final String USE_WILDCARD = "essentials.warp.use.*";

  private final WarpStore store;

  public Optional<Warp> find(@NonNull String name) {
    return this.store.find(name);
  }

  public List<Warp> list() {
    return this.store.list();
  }

  public List<Warp> listVisibleTo(@NonNull Permissible permissible) {
    return this.store.list().stream().filter(warp -> canUse(permissible, warp.name())).toList();
  }

  public void save(@NonNull String name, @NonNull Player creator) {
    var location = creator.getLocation();
    var uniqueId = creator.getUniqueId();
    var warp = Warp.of(name, location, uniqueId);

    this.store.save(warp);
  }

  public boolean delete(@NonNull String name) {
    return this.store.delete(name);
  }

  /** Whether {@code permissible} may use the warp named {@code name}. */
  public boolean canUse(@NonNull Permissible permissible, @NonNull String name) {
    if (permissible.hasPermission(USE_WILDCARD)) {
      return true;
    }

    var permissionNode = USE_PREFIX + name.toLowerCase(Locale.ROOT);
    return permissible.hasPermission(permissionNode);
  }
}
