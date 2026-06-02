package com.hanielcota.essentials.modules.ban.repository;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.ban.domain.ActiveBan;
import com.hanielcota.essentials.modules.ban.domain.Ban;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Write-through {@link BanRepository} that answers lookups from a {@link BanCache} populated at
 * module enable. Expired-on-read entries are swept from the cache and a delete is enqueued to the
 * async writer.
 */
@RequiredArgsConstructor
public final class CachedBanRepository implements BanRepository {

  private final @NonNull BanRepository delegate;
  private final @NonNull BanCache cache;
  private final @NonNull AsyncDatabaseWriter writer;

  @Override
  public List<ActiveBan> listActive(@NonNull Instant now) {
    var active = this.cache.all();

    return active.stream().filter(entry -> !entry.ban().isExpired(now)).toList();
  }

  @Override
  public Optional<Ban> findActive(@NonNull UUID id, @NonNull Instant now) {
    var handle = this.cache.find(id);
    if (handle.isEmpty()) {
      return Optional.empty();
    }

    var entry = handle.get();
    var ban = entry.ban();
    if (!ban.isExpired(now)) {
      return Optional.of(ban);
    }

    if (this.cache.remove(id, entry)) {
      Runnable persist = () -> this.delegate.delete(id);
      this.writer.submit("delete expired ban", persist);
    }

    return Optional.empty();
  }

  @Override
  public void save(@NonNull ActiveBan ban) {
    this.cache.put(ban);

    Runnable persist = () -> this.delegate.save(ban);
    this.writer.submit("save ban", persist);
  }

  @Override
  public boolean delete(@NonNull UUID id) {
    var removed = this.cache.remove(id);
    if (!removed) {
      return false;
    }

    Runnable persist = () -> this.delegate.delete(id);
    this.writer.submit("delete ban", persist);

    return true;
  }

  @Override
  public int deleteExpired(@NonNull Instant now) {
    return this.delegate.deleteExpired(now);
  }
}
