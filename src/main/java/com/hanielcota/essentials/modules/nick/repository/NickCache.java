package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Pure in-memory state holder for nicknames. No persistence orchestration lives here — write-through
 * to SQL is the job of {@link CachedNickRepository}, which uses this cache as its lookup surface.
 */
public final class NickCache {

  private final ConcurrentHashMap<UUID, NickEntry> byId = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, UUID> idByLowerNick = new ConcurrentHashMap<>();

  public void loadAll(@NonNull List<NickEntry> entries) {
    for (var entry : entries) {
      put(entry);
    }
  }

  public Optional<NickEntry> findById(@NonNull UUID id) {
    var entry = this.byId.get(id);
    return Optional.ofNullable(entry);
  }

  public Optional<UUID> idByNickname(@NonNull String nickname) {
    var key = nickname.toLowerCase(Locale.ROOT);
    var id = this.idByLowerNick.get(key);
    return Optional.ofNullable(id);
  }

  public boolean isTakenByOther(@NonNull String nickname, @NonNull UUID self) {
    var key = nickname.toLowerCase(Locale.ROOT);
    var owner = this.idByLowerNick.get(key);

    if (owner == null) {
      return false;
    }

    return !owner.equals(self);
  }

  public void put(@NonNull NickEntry entry) {
    var previous = this.byId.put(entry.id(), entry);
    if (previous != null) {
      var previousKey = previous.nickname().toLowerCase(Locale.ROOT);
      this.idByLowerNick.remove(previousKey, previous.id());
    }

    var key = entry.nickname().toLowerCase(Locale.ROOT);
    this.idByLowerNick.put(key, entry.id());
  }

  public Optional<NickEntry> remove(@NonNull UUID id) {
    var previous = this.byId.remove(id);
    if (previous == null) {
      return Optional.empty();
    }

    var key = previous.nickname().toLowerCase(Locale.ROOT);
    this.idByLowerNick.remove(key, previous.id());

    return Optional.of(previous);
  }
}
