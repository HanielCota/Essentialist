package com.hanielcota.essentials.modules.kit.repository;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import java.util.Map;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Caching {@link KitUsageRepository}: reads hit the in-memory bucket (loaded on pre-login) so menu
 * rendering never blocks on SQL; writes update the cache synchronously and enqueue the persist to
 * the async writer thread.
 */
@RequiredArgsConstructor
public final class CachedKitUsageRepository implements KitUsageRepository, AutoCloseable {

  private final KitUsageRepository delegate;
  private final AsyncDatabaseWriter writer;
  private final KitUsageCache cache;

  /** Loads {@code player}'s usage from SQL into the cache. Blocks the calling thread. */
  public void loadFor(@NonNull UUID player) {
    var usage = this.delegate.findAll(player);
    this.cache.loadFor(player, usage);
  }

  public void evictFor(@NonNull UUID player) {
    this.cache.evictFor(player);
  }

  @Override
  public Map<String, Long> findAll(@NonNull UUID player) {
    ensureLoaded(player);

    return this.cache.all(player);
  }

  @Override
  public void upsert(@NonNull UUID player, @NonNull String kitId, long usedAtMs) {
    this.cache.put(player, kitId, usedAtMs);

    Runnable persist = () -> this.delegate.upsert(player, kitId, usedAtMs);
    this.writer.submit("upsert kit usage", persist);
  }

  @Override
  public void deleteKit(@NonNull String kitId) {
    this.cache.removeKit(kitId);

    Runnable persist = () -> this.delegate.deleteKit(kitId);
    this.writer.submit("delete kit usage", persist);
  }

  @Override
  public void close() {
    this.writer.close();
  }

  private void ensureLoaded(@NonNull UUID player) {
    if (this.cache.isLoaded(player)) {
      return;
    }

    loadFor(player);
  }
}
