package com.hanielcota.essentials.modules.nick.repository;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Write-through {@link NickRepository} that answers lookups from a {@link NickCache} populated at
 * module enable, then submits SQL persistence to the async writer. Reads never touch the DB. The
 * full nick table is small enough to load at boot, so there is no fallback path through the
 * delegate for missing entries.
 */
@RequiredArgsConstructor
public final class CachedNickRepository implements NickRepository {

  private final @NonNull NickRepository delegate;
  private final @NonNull NickCache cache;
  private final @NonNull AsyncDatabaseWriter writer;

  @Override
  public List<NickEntry> list() {
    return this.delegate.list();
  }

  @Override
  public Optional<NickEntry> findById(@NonNull UUID id) {
    return this.cache.findById(id);
  }

  @Override
  public Optional<UUID> idByNickname(@NonNull String nickname) {
    return this.cache.idByNickname(nickname);
  }

  @Override
  public boolean isTakenByOther(@NonNull String nickname, @NonNull UUID self) {
    return this.cache.isTakenByOther(nickname, self);
  }

  @Override
  public void save(@NonNull NickEntry entry) {
    this.cache.put(entry);

    Runnable persist = () -> this.delegate.save(entry);
    this.writer.submit("save nick", persist);
  }

  @Override
  public boolean delete(@NonNull UUID id) {
    var removed = this.cache.remove(id);
    if (removed.isEmpty()) {
      return false;
    }

    Runnable persist = () -> this.delegate.delete(id);
    this.writer.submit("delete nick", persist);

    return true;
  }
}
