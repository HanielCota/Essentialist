package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.modules.warps.repository.WarpCache;
import com.hanielcota.essentials.modules.warps.repository.WarpRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

/**
 * Application service for the warps use cases.
 *
 * <p>Reads come from {@link WarpCache} (no SQL on the hot path), writes update the cache
 * synchronously and queue the SQL persist on {@link AsyncDatabaseWriter}. Permission gating on
 * {@code essentials.warp.use.<name>} (lowercased) — or the bypass node {@code
 * essentials.warp.use.*} — stays here so menu and command paths agree.
 */
@RequiredArgsConstructor
public final class WarpService {

  private static final String USE_PREFIX = "essentials.warp.use.";
  private static final String USE_WILDCARD = "essentials.warp.use.*";

  private final WarpRepository repository;
  private final WarpCache cache;
  private final AsyncDatabaseWriter writer;

  public Optional<Warp> find(@NonNull String name) {
    return this.cache.find(name);
  }

  public List<Warp> list() {
    return this.cache.list();
  }

  public List<Warp> listVisibleTo(@NonNull Permissible permissible) {
    var allWarps = this.cache.list();
    Predicate<Warp> usableByPermissible = warp -> canUse(permissible, warp.name());

    return allWarps.stream().filter(usableByPermissible).toList();
  }

  public void save(@NonNull String name, @NonNull Player creator) {
    var location = creator.getLocation();
    var uniqueId = creator.getUniqueId();
    var warp = Warp.of(name, location, uniqueId);

    this.cache.put(warp);

    Runnable persist = () -> this.repository.save(warp);
    this.writer.submit("save warp", persist);
  }

  public boolean delete(@NonNull String name) {
    var removed = this.cache.remove(name);
    if (removed.isEmpty()) {
      return false;
    }

    var canonicalName = removed.get().name();

    Runnable persist = () -> this.repository.delete(canonicalName);
    this.writer.submit("delete warp", persist);

    return true;
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
