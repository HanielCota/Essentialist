package com.hanielcota.essentials.modules.spawn.service;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.spawn.domain.SpawnLocation;
import com.hanielcota.essentials.modules.spawn.repository.SpawnRepository;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;

/**
 * Reads and writes the server spawn, with the latest value cached in memory so {@code /spawn} never
 * blocks on disk.
 *
 * <p>Sole responsibility: keep the cache and the {@link SpawnRepository} in sync. Reads return the
 * cached value; {@link #set} writes the cache synchronously (so the next {@code /spawn} sees the
 * new value immediately) and queues the SQL persist on {@link AsyncDatabaseWriter} so the calling
 * command does not stall on disk I/O.
 */
public final class SpawnService {

  private final SpawnRepository store;
  private final AsyncDatabaseWriter writer;
  private final AtomicReference<SpawnLocation> cached = new AtomicReference<>();

  public SpawnService(@NonNull SpawnRepository store, @NonNull AsyncDatabaseWriter writer) {
    this.store = store;
    this.writer = writer;
    store.load().ifPresent(cached::set);
  }

  /** The configured spawn, or empty when {@code /setspawn} has not run yet. */
  public Optional<SpawnLocation> current() {
    return Optional.ofNullable(this.cached.get());
  }

  /** Updates the cache immediately and queues a write-through to SQLite on the writer thread. */
  public void set(@NonNull SpawnLocation location) {
    this.cached.set(location);

    Runnable persistTask = () -> this.store.save(location);
    this.writer.submit("save spawn", persistTask);
  }
}
