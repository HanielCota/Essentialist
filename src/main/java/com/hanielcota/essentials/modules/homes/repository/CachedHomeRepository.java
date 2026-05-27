package com.hanielcota.essentials.modules.homes.repository;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
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
 *
 * <p>Reads ({@link #find}/{@link #list}/{@link #count}) fall back to the delegate (SQL) when the
 * bucket isn't loaded — happens at most once per owner per session and keeps the {@link
 * HomeRepository} contract honest (no silent empty returns when the bucket is missing).
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
   * Drops {@code owner} from the in-memory cache.
   *
   * <p>Runs synchronously on the caller's thread (typically PlayerQuitEvent on the main thread).
   * The cache is independent of pending SQL writes — every {@link #save}/{@link #delete}/{@link
   * #rename}/{@link #updateMaterial} captures its payload before submitting to the writer — so
   * eviction does not need to wait for the writer to drain. Deferring it through the writer queue
   * would race a fresh {@link #loadFor} from a quick reconnect and wipe the freshly loaded bucket.
   */
  public void evictFor(@NonNull UUID owner) {
    this.cache.evictFor(owner);
  }

  @Override
  public Optional<Home> find(@NonNull UUID owner, @NonNull String name) {
    ensureLoaded(owner);

    return this.cache.find(owner, name);
  }

  @Override
  public List<Home> list(@NonNull UUID owner) {
    ensureLoaded(owner);

    return this.cache.list(owner);
  }

  @Override
  public int count(@NonNull UUID owner) {
    ensureLoaded(owner);

    return this.cache.count(owner);
  }

  @Override
  public void save(@NonNull Home home) {
    this.cache.save(home);

    Runnable persist = () -> this.delegate.save(home);
    this.writer.submit("save home", persist);
  }

  @Override
  public boolean delete(@NonNull UUID owner, @NonNull String name) {
    var removed = this.cache.delete(owner, name);

    if (removed.isEmpty()) {
      return false;
    }

    var actualHome = removed.get();
    var actualName = actualHome.name();

    Runnable persist = () -> this.delegate.delete(owner, actualName);
    this.writer.submit("delete home", persist);

    return true;
  }

  @Override
  public boolean rename(@NonNull UUID owner, @NonNull String oldName, @NonNull String newName) {
    var renamed = this.cache.rename(owner, oldName, newName);

    if (renamed.isEmpty()) {
      return false;
    }

    Runnable persist = () -> this.delegate.rename(owner, oldName, newName);
    this.writer.submit("rename home", persist);

    return true;
  }

  @Override
  public boolean updateMaterial(
      @NonNull UUID owner, @NonNull String name, @NonNull Material material) {
    var updated = this.cache.updateMaterial(owner, name, material);

    if (updated.isEmpty()) {
      return false;
    }

    Runnable persist = () -> this.delegate.updateMaterial(owner, name, material);
    this.writer.submit("update home material", persist);

    return true;
  }

  @Override
  public boolean updatePinned(@NonNull UUID owner, @NonNull String name, boolean pinned) {
    var updated = this.cache.updatePinned(owner, name, pinned);

    if (updated.isEmpty()) {
      return false;
    }

    Runnable persist = () -> this.delegate.updatePinned(owner, name, pinned);
    this.writer.submit("update home pinned", persist);

    return true;
  }

  @Override
  public boolean bumpUsage(@NonNull UUID owner, @NonNull String name, long timestampMs) {
    var updated = this.cache.bumpUsage(owner, name, timestampMs);

    if (updated.isEmpty()) {
      return false;
    }

    Runnable persist = () -> this.delegate.bumpUsage(owner, name, timestampMs);
    this.writer.submit("bump home usage", persist);

    return true;
  }

  @Override
  public void close() {
    this.writer.close();
  }

  private void ensureLoaded(@NonNull UUID owner) {
    if (this.cache.isLoaded(owner)) {
      return;
    }

    loadFor(owner);
  }
}
