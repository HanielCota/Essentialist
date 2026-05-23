package com.hanielcota.essentials.modules.homes.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Application service for the homes use cases.
 *
 * <p>Sole responsibility: enforce the per-player limit and delegate persistence to {@link
 * HomeStore}. Owns no state of its own; reads ({@link #find}, {@link #list}) hit the store
 * directly.
 */
public final class HomeService {

  private final HomeStore store;
  private final HomeLimitResolver limits;

  public HomeService(HomeStore store, HomeLimitResolver limits) {
    this.store = Objects.requireNonNull(store, "store");
    this.limits = Objects.requireNonNull(limits, "limits");
  }

  public Optional<Home> find(UUID owner, String name) {
    return store.find(owner, name);
  }

  public List<Home> list(UUID owner) {
    return store.list(owner);
  }

  public int count(UUID owner) {
    return store.count(owner);
  }

  public int limit(Player owner) {
    return limits.resolve(owner);
  }

  /**
   * Saves the home. Returns the outcome — {@link SaveResult#CREATED} for new homes, {@link
   * SaveResult#UPDATED} when overwriting an existing name, {@link SaveResult#LIMIT_REACHED} when
   * the player has no free slot for a new home.
   */
  public SaveResult save(Player owner, String name, Location location) {
    Objects.requireNonNull(owner, "owner");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(location, "location");

    var ownerId = owner.getUniqueId();
    if (store.find(ownerId, name).isPresent()) {
      store.save(Home.of(ownerId, name, location));
      return SaveResult.UPDATED;
    }
    if (store.count(ownerId) >= limits.resolve(owner)) {
      return SaveResult.LIMIT_REACHED;
    }
    store.save(Home.of(ownerId, name, location));
    return SaveResult.CREATED;
  }

  /** Deletes the home. Returns {@code true} when a row was removed. */
  public boolean delete(UUID owner, String name) {
    return store.delete(owner, name);
  }

  public enum SaveResult {
    CREATED,
    UPDATED,
    LIMIT_REACHED
  }
}
