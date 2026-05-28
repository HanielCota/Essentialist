package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.api.WarpsApi;
import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.modules.warps.repository.WarpCache;
import com.hanielcota.essentials.modules.warps.repository.WarpRepository;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

/**
 * Application service for the warps use cases: cache-backed reads, cache-then-async writes. The
 * per-warp access rule lives in {@link WarpPermissions}; this service just delegates to it so menu
 * and command paths agree.
 */
@RequiredArgsConstructor
public final class WarpService implements WarpsApi {

  private final WarpRepository repository;
  private final WarpCache cache;
  private final AsyncDatabaseWriter writer;
  private final WarpFavorites favorites;
  private final WarpLikes likes;

  private final WarpPermissions permissions = new WarpPermissions();

  public Optional<Warp> findWarp(@NonNull String name) {
    return this.cache.find(name);
  }

  public List<Warp> warps() {
    return this.cache.list();
  }

  public List<Warp> visibleTo(@NonNull Permissible permissible) {
    var allWarps = this.cache.list();
    Predicate<Warp> usableByPermissible = warp -> canUse(permissible, warp.name());

    return allWarps.stream().filter(usableByPermissible).toList();
  }

  /** Whether {@code permissible} may use the warp named {@code name}. */
  public boolean canUse(@NonNull Permissible permissible, @NonNull String name) {
    return this.permissions.canUse(permissible, name);
  }

  public void save(@NonNull String name, @NonNull Player creator, @NonNull Material icon) {
    var location = creator.getLocation();
    var existing = this.cache.find(name);

    // Overwriting (/setwarp on an existing warp) relocates it but keeps the original creation
    // time and author; only a brand-new warp stamps the current creator and time.
    var warp = build(name, location, creator, icon, existing);

    this.cache.put(warp);

    Runnable persist = () -> this.repository.save(warp);
    this.writer.submit("save warp", persist);
  }

  private static Warp build(
      @NonNull String name,
      @NonNull Location location,
      @NonNull Player creator,
      @NonNull Material icon,
      @NonNull Optional<Warp> existing) {
    if (existing.isEmpty()) {
      var uniqueId = creator.getUniqueId();
      return Warp.of(name, location, uniqueId, icon);
    }

    var previous = existing.get();
    return previous.movedTo(location, icon);
  }

  public boolean delete(@NonNull String name) {
    var removed = this.cache.remove(name);
    if (removed.isEmpty()) {
      return false;
    }

    var canonicalName = removed.get().name();

    Runnable persist = () -> this.repository.delete(canonicalName);
    this.writer.submit("delete warp", persist);

    // Drop the social data so a warp recreated with the same name starts clean and the join
    // tables don't accumulate orphan rows.
    this.favorites.forgetWarp(canonicalName);
    this.likes.forgetWarp(canonicalName);

    return true;
  }
}
