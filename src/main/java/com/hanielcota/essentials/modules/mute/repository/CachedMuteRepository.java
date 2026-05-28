package com.hanielcota.essentials.modules.mute.repository;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.mute.domain.Mute;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Write-through {@link MuteRepository} that answers lookups from a {@link MuteCache} populated at
 * module enable. Expired-on-read entries are swept from the cache and a delete is enqueued to the
 * async writer.
 */
@RequiredArgsConstructor
public final class CachedMuteRepository implements MuteRepository {

  private final @NonNull MuteRepository delegate;
  private final @NonNull MuteCache cache;
  private final @NonNull AsyncDatabaseWriter writer;

  @Override
  public List<Map.Entry<UUID, Mute>> listActive(@NonNull Instant now) {
    return this.delegate.listActive(now);
  }

  @Override
  public Optional<Mute> findActive(@NonNull UUID id, @NonNull Instant now) {
    var muteHandle = this.cache.find(id);
    if (muteHandle.isEmpty()) {
      return Optional.empty();
    }

    var mute = muteHandle.get();
    if (!mute.isExpired(now)) {
      return muteHandle;
    }

    if (this.cache.remove(id, mute)) {
      Runnable persist = () -> this.delegate.delete(id);
      this.writer.submit("delete expired mute", persist);
    }

    return Optional.empty();
  }

  @Override
  public void save(@NonNull UUID id, @NonNull Mute mute) {
    this.cache.put(id, mute);

    Runnable persist = () -> this.delegate.save(id, mute);
    this.writer.submit("save mute", persist);
  }

  @Override
  public boolean delete(@NonNull UUID id) {
    var removed = this.cache.remove(id);
    if (!removed) {
      return false;
    }

    Runnable persist = () -> this.delegate.delete(id);
    this.writer.submit("delete mute", persist);

    return true;
  }

  @Override
  public int deleteExpired(@NonNull Instant now) {
    return this.delegate.deleteExpired(now);
  }
}
