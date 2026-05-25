package com.hanielcota.essentials.modules.nick.service;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.nick.model.NickEntry;
import com.hanielcota.essentials.modules.nick.repository.NickStore;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * In-memory registry of nickname assignments backed by {@link NickStore}.
 *
 * <p>Holds two caches: id → entry for forward lookups, and lower-cased nickname → id for /realname
 * reverse lookups. Both are mutated together so they cannot diverge.
 *
 * <p>Writes update the cache synchronously and queue the SQL persist via {@link
 * AsyncDatabaseWriter}.
 */
@RequiredArgsConstructor
public final class NickService {

  private final NickStore store;
  private final AsyncDatabaseWriter writer;
  private final ConcurrentHashMap<UUID, NickEntry> byId = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, UUID> idByLowerNick = new ConcurrentHashMap<>();

  public void loadAll(@NonNull List<NickEntry> entries) {
    for (var entry : entries) {
      cacheInsert(entry);
    }
  }

  public Optional<NickEntry> nickFor(@NonNull UUID id) {
    var entry = this.byId.get(id);

    return Optional.ofNullable(entry);
  }

  public Optional<UUID> idByNick(@NonNull String nickname) {
    var key = nickname.toLowerCase(Locale.ROOT);
    var id = this.idByLowerNick.get(key);

    return Optional.ofNullable(id);
  }

  /** Whether {@code nickname} is already taken by someone other than {@code self}. */
  public boolean isTakenByOther(@NonNull String nickname, @NonNull UUID self) {
    var key = nickname.toLowerCase(Locale.ROOT);
    var owner = this.idByLowerNick.get(key);

    if (owner == null) {
      return false;
    }

    return !owner.equals(self);
  }

  public void set(@NonNull UUID id, @NonNull String nickname, @NonNull String realName) {
    var entry = new NickEntry(id, nickname, realName);
    cacheInsert(entry);

    Runnable persist = () -> this.store.save(entry);
    this.writer.submit("save nick", persist);
  }

  public boolean reset(@NonNull UUID id) {
    var previous = cacheRemove(id);
    if (previous == null) {
      return false;
    }

    Runnable persist = () -> this.store.delete(id);
    this.writer.submit("delete nick", persist);

    return true;
  }

  private void cacheInsert(@NonNull NickEntry entry) {
    var previous = this.byId.put(entry.id(), entry);
    if (previous != null) {
      var previousKey = previous.nickname().toLowerCase(Locale.ROOT);
      this.idByLowerNick.remove(previousKey, previous.id());
    }

    var key = entry.nickname().toLowerCase(Locale.ROOT);
    this.idByLowerNick.put(key, entry.id());
  }

  private NickEntry cacheRemove(@NonNull UUID id) {
    var previous = this.byId.remove(id);
    if (previous == null) {
      return null;
    }

    var key = previous.nickname().toLowerCase(Locale.ROOT);
    this.idByLowerNick.remove(key, previous.id());

    return previous;
  }
}
