package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

/**
 * Runtime cache for homes.
 *
 * <p>Each player's bucket is loaded on demand via {@link #loadFor} (call from a thread that is safe
 * to block — typically the AsyncPlayerPreLoginEvent thread) and evicted via {@link #evictFor} on
 * quit. Mutations write through to the cache synchronously and enqueue the SQL persist to the
 * writer thread.
 */
@RequiredArgsConstructor
public final class CachedHomeRepository implements HomeRepository, AutoCloseable {

  private final HomeRepository delegate;
  private final AsyncDatabaseWriter writer;
  private final HomeCache cache;

  /** Loads {@code owner}'s homes from SQL and populates the cache. Blocks the calling thread. */
  public void loadFor(@NonNull UUID owner) {
    var homes = this.delegate.list(owner);
    this.cache.loadFor(owner, homes);
  }

  /**
   * Drops {@code owner} from the in-memory cache after pending writes drain.
   *
   * <p>Deferred through the single-threaded writer queue so a rapid disconnect/reconnect cannot
   * read SQL state older than the cache that just got evicted: any queued save/delete/rename for
   * this owner runs first, then the eviction.
   */
  public void evictFor(@NonNull UUID owner) {
    this.writer.submit("evict home cache", () -> this.cache.evictFor(owner));
  }

  @Override
  public Optional<Home> find(@NonNull UUID owner, @NonNull String name) {
    return this.cache.find(owner, name);
  }

  @Override
  public List<Home> list(@NonNull UUID owner) {
    return this.cache.list(owner);
  }

  @Override
  public int count(@NonNull UUID owner) {
    return this.cache.count(owner);
  }

  @Override
  public void save(@NonNull Home home) {
    this.cache.save(home);
    this.writer.submit("save home", () -> this.delegate.save(home));
  }

  @Override
  public boolean delete(@NonNull UUID owner, @NonNull String name) {
    var removed = this.cache.delete(owner, name);

    if (removed.isEmpty()) {
      return false;
    }

    var actualHome = removed.get();
    this.writer.submit("delete home", () -> this.delegate.delete(owner, actualHome.name()));

    return true;
  }

  @Override
  public boolean rename(@NonNull UUID owner, @NonNull String oldName, @NonNull String newName) {
    var renamed = this.cache.rename(owner, oldName, newName);

    if (renamed.isEmpty()) {
      return false;
    }

    this.writer.submit("rename home", () -> this.delegate.rename(owner, oldName, newName));

    return true;
  }

  @Override
  public boolean updateMaterial(
      @NonNull UUID owner, @NonNull String name, @NonNull Material material) {
    var updated = this.cache.updateMaterial(owner, name, material);

    if (updated.isEmpty()) {
      return false;
    }

    this.writer.submit(
        "update home material", () -> this.delegate.updateMaterial(owner, name, material));

    return true;
  }

  @Override
  public void close() {
    this.writer.close();
  }
}
