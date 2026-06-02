package com.hanielcota.essentials.modules.ban.repository;

import com.hanielcota.essentials.modules.ban.domain.ActiveBan;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Pure in-memory state holder for active bans. Expiry sweep and persistence orchestration live in
 * {@link CachedBanRepository}; this class is a plain {@code ConcurrentHashMap} adapter.
 */
public final class BanCache {

  private final ConcurrentHashMap<UUID, ActiveBan> entries = new ConcurrentHashMap<>();

  public void loadAll(@NonNull List<ActiveBan> rows) {
    for (var row : rows) {
      this.entries.put(row.id(), row);
    }
  }

  public Optional<ActiveBan> find(@NonNull UUID id) {
    var ban = this.entries.get(id);
    return Optional.ofNullable(ban);
  }

  public Collection<ActiveBan> all() {
    return List.copyOf(this.entries.values());
  }

  public void put(@NonNull ActiveBan ban) {
    this.entries.put(ban.id(), ban);
  }

  public boolean remove(@NonNull UUID id, @NonNull ActiveBan expected) {
    return this.entries.remove(id, expected);
  }

  public boolean remove(@NonNull UUID id) {
    return this.entries.remove(id) != null;
  }
}
