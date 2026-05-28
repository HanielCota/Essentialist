package com.hanielcota.essentials.modules.mute.repository;

import com.hanielcota.essentials.modules.mute.domain.Mute;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Pure in-memory state holder for active mutes. Expiry sweep and persistence orchestration live in
 * {@link CachedMuteRepository}; this class is a plain {@code ConcurrentHashMap} adapter.
 */
public final class MuteCache {

  private final ConcurrentHashMap<UUID, Mute> entries = new ConcurrentHashMap<>();

  public void loadAll(@NonNull List<Map.Entry<UUID, Mute>> rows) {
    for (var row : rows) {
      this.entries.put(row.getKey(), row.getValue());
    }
  }

  public Optional<Mute> find(@NonNull UUID id) {
    var mute = this.entries.get(id);
    return Optional.ofNullable(mute);
  }

  public void put(@NonNull UUID id, @NonNull Mute mute) {
    this.entries.put(id, mute);
  }

  public boolean remove(@NonNull UUID id, @NonNull Mute expected) {
    return this.entries.remove(id, expected);
  }

  public boolean remove(@NonNull UUID id) {
    return this.entries.remove(id) != null;
  }
}
