package com.hanielcota.essentials.modules.mute.service;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.modules.mute.repository.MuteStore;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public final class MuteCache {

  private final MuteStore store;
  private final AsyncDatabaseWriter writer;
  private final ConcurrentHashMap<UUID, Mute> cache = new ConcurrentHashMap<>();

  public MuteCache(@NonNull MuteStore store, @NonNull AsyncDatabaseWriter writer) {
    this.store = store;
    this.writer = writer;
  }

  public void loadAll(@NonNull List<Map.Entry<UUID, Mute>> rows) {
    for (var row : rows) {
      this.cache.put(row.getKey(), row.getValue());
    }
  }

  Optional<Mute> activeMute(@NonNull UUID id) {
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

  void apply(@NonNull UUID id, @NonNull Mute mute) {
    this.cache.put(id, mute);

    Runnable persist = () -> this.store.save(id, mute);
    this.writer.submit("save mute", persist);
  }

  boolean remove(@NonNull UUID id) {
    var previous = this.cache.remove(id);
    if (previous == null) {
      return false;
    }

    Runnable persist = () -> this.store.delete(id);
    this.writer.submit("delete mute", persist);

    return true;
  }
}
