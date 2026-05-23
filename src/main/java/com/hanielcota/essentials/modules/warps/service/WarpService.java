package com.hanielcota.essentials.modules.warps.service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

/**
 * Application service for the warps use cases.
 *
 * <p>Sole responsibility: delegate persistence to {@link WarpStore} and apply the per-warp
 * permission check {@code essentials.warp.use.<name>} (lowercased) — or the bypass node {@code
 * essentials.warp.use.*}.
 */
public final class WarpService {

  private static final String USE_PREFIX = "essentials.warp.use.";
  private static final String USE_WILDCARD = "essentials.warp.use.*";

  private final WarpStore store;

  public WarpService(WarpStore store) {
    this.store = Objects.requireNonNull(store, "store");
  }

  public Optional<Warp> find(String name) {
    return store.find(name);
  }

  public List<Warp> list() {
    return store.list();
  }

  public List<Warp> listVisibleTo(Permissible permissible) {
    Objects.requireNonNull(permissible, "permissible");
    return store.list().stream().filter(warp -> canUse(permissible, warp.name())).toList();
  }

  public void save(String name, Player creator) {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(creator, "creator");
    store.save(Warp.of(name, creator.getLocation(), creator.getUniqueId()));
  }

  public boolean delete(String name) {
    return store.delete(name);
  }

  /** Whether {@code permissible} may use the warp named {@code name}. */
  public boolean canUse(Permissible permissible, String name) {
    Objects.requireNonNull(permissible, "permissible");
    Objects.requireNonNull(name, "name");
    return permissible.hasPermission(USE_WILDCARD)
        || permissible.hasPermission(USE_PREFIX + name.toLowerCase(Locale.ROOT));
  }
}
