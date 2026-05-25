package com.hanielcota.essentials.modules.homes.repository;

import java.util.UUID;
import lombok.NonNull;

/**
 * Lifecycle hooks for the homes cache: load on player connect, evict on disconnect. Lets the
 * listener depend on a narrow contract instead of the concrete {@link CachedHomeRepository}.
 */
public interface HomeCacheLifecycle {

  /** Loads {@code owner}'s homes from the underlying store and populates the cache. */
  void loadFor(@NonNull UUID owner);

  /** Drops {@code owner}'s bucket from memory. */
  void evictFor(@NonNull UUID owner);
}
