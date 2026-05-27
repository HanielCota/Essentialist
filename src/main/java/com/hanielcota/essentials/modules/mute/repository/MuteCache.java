package com.hanielcota.essentials.modules.mute.repository;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.mute.domain.Mute;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class MuteCache {

  private final @NonNull MuteRepository repository;
  private final @NonNull AsyncDatabaseWriter writer;
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
      Runnable persist = () -> this.repository.delete(id);
      this.writer.submit("delete expired mute", persist);
    }

    return Optional.empty();
  }

  public void apply(@NonNull UUID id, @NonNull Mute mute) {
    this.cache.put(id, mute);

    Runnable persist = () -> this.repository.save(id, mute);
    this.writer.submit("save mute", persist);
  }

  public boolean remove(@NonNull UUID id) {
    var previous = this.cache.remove(id);
    if (previous == null) {
      return false;
    }

    Runnable persist = () -> this.repository.delete(id);
    this.writer.submit("delete mute", persist);

    return true;
  }
}
