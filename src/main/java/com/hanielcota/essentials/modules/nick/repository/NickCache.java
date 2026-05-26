package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public final class NickCache {

  private final NickRepository repository;
  private final AsyncDatabaseWriter writer;
  private final ConcurrentHashMap<UUID, NickEntry> byId = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, UUID> idByLowerNick = new ConcurrentHashMap<>();

  public NickCache(@NonNull NickRepository repository, @NonNull AsyncDatabaseWriter writer) {
    this.repository = repository;
    this.writer = writer;
  }

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

    Runnable persist = () -> this.repository.save(entry);
    this.writer.submit("save nick", persist);
  }

  public boolean reset(@NonNull UUID id) {
    var previous = cacheRemove(id);
    if (previous == null) {
      return false;
    }

    Runnable persist = () -> this.repository.delete(id);
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
