package com.hanielcota.essentials.modules.spawn.service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;

/**
 * Reads and writes the server spawn, with the latest value cached in memory so {@code /spawn} never
 * blocks on disk.
 *
 * <p>Sole responsibility: keep the cache and the {@link SpawnStore} in sync. Reads return the
 * cached value; {@link #set} writes through to the store before updating the cache so a failure
 * propagates instead of leaving a dirty in-memory copy.
 */
public final class SpawnService {

  private final SpawnStore store;
  private final AtomicReference<SpawnLocation> cached = new AtomicReference<>();

  public SpawnService(SpawnStore store) {
    this.store = store;
    store.load().ifPresent(cached::set);
  }

  /** The configured spawn, or empty when {@code /setspawn} has not run yet. */
  public Optional<SpawnLocation> current() {
    return Optional.ofNullable(this.cached.get());
  }

  /** Persists {@code location} as the spawn point and refreshes the cache. */
  public void set(@NonNull SpawnLocation location) {
    this.store.save(location);
    this.cached.set(location);
  }
}
