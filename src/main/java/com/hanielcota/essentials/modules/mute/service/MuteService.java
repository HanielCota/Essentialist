package com.hanielcota.essentials.modules.mute.service;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.mute.model.Mute;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * In-memory cache of active mutes backed by {@link MuteStore}. Reads come from the cache (no SQL on
 * the hot path — the chat listener calls {@link #activeMute(UUID)} on every chat message). Writes
 * update the cache synchronously and queue the SQL persist on {@link AsyncDatabaseWriter}.
 *
 * <p>Expired timed mutes are evicted lazily by {@link #activeMute(UUID)} so callers never see a
 * stale mute, and a {@code DELETE} is queued so the table doesn't keep stale rows between restarts.
 */
@RequiredArgsConstructor
public final class MuteService {

  private final MuteStore store;
  private final AsyncDatabaseWriter writer;
  private final ConcurrentHashMap<UUID, Mute> cache = new ConcurrentHashMap<>();

  public void loadAll(@NonNull List<Map.Entry<UUID, Mute>> rows) {
    for (var row : rows) {
      this.cache.put(row.getKey(), row.getValue());
    }
  }

  public Optional<Mute> activeMute(@NonNull UUID id) {
    var mute = this.cache.get(id);
    if (mute == null) {
      return Optional.empty();
    }

    var now = Instant.now();
    if (!mute.isExpired(now)) {
      return Optional.of(mute);
    }

    var removed = this.cache.remove(id, mute);
    if (removed) {
      Runnable persist = () -> this.store.delete(id);
      this.writer.submit("delete expired mute", persist);
    }

    return Optional.empty();
  }

  public void mute(@NonNull UUID id, @NonNull Mute mute) {
    this.cache.put(id, mute);

    Runnable persist = () -> this.store.save(id, mute);
    this.writer.submit("save mute", persist);
  }

  public boolean unmute(@NonNull UUID id) {
    var previous = this.cache.remove(id);
    if (previous == null) {
      return false;
    }

    Runnable persist = () -> this.store.delete(id);
    this.writer.submit("delete mute", persist);

    return true;
  }
}
