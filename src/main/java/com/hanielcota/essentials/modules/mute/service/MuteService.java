package com.hanielcota.essentials.modules.mute.service;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.modules.mute.repository.MuteRepository;
import io.github.hanielcota.commandframework.core.util.TimeParser;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * In-memory cache of active mutes backed by {@link MuteRepository}. Reads come from the cache (no
 * SQL on the hot path — the chat listener calls {@link #activeMute(UUID)} on every chat message).
 * Writes update the cache synchronously and queue the SQL persist on {@link AsyncDatabaseWriter}.
 *
 * <p>Expired timed mutes are evicted lazily by {@link #activeMute(UUID)} so callers never see a
 * stale mute, and a {@code DELETE} is queued so the table doesn't keep stale rows between restarts.
 *
 * <p>{@link #mute(Player, String)} owns the {@code essentials.mute.exempt} check, the duration
 * parsing and the {@link Mute} factory so commands stay thin.
 */
@RequiredArgsConstructor
public final class MuteService {

  public static final String EXEMPT_PERMISSION = "essentials.mute.exempt";

  private final MuteRepository store;
  private final AsyncDatabaseWriter writer;
  private final ConcurrentHashMap<UUID, Mute> cache = new ConcurrentHashMap<>();

  private static @Nullable Duration tryParseDuration(@NonNull String input) {
    try {
      return TimeParser.parse(input);
    } catch (RuntimeException ignored) {
      return null;
    }
  }

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
      Runnable persist = () -> this.store.delete(id);
      this.writer.submit("delete expired mute", persist);
    }

    return Optional.empty();
  }

  /**
   * Applies a mute to {@code target}. Returns one of {@link MuteOutcome.Exempt}, {@link
   * MuteOutcome.InvalidDuration} or {@link MuteOutcome.Muted}. An empty {@code rawDuration}
   * produces a permanent mute.
   */
  public MuteOutcome mute(@NonNull Player target, @NonNull String rawDuration) {
    var name = target.getName();
    if (target.hasPermission(EXEMPT_PERMISSION)) {
      return new MuteOutcome.Exempt(name);
    }

    var trimmed = rawDuration.strip();
    Duration duration = null;
    if (!trimmed.isEmpty()) {
      duration = tryParseDuration(trimmed);
      if (duration == null) {
        return new MuteOutcome.InvalidDuration(trimmed);
      }
    }

    var now = Instant.now();
    var mute = Mute.from(duration, now);
    var id = target.getUniqueId();
    applyMute(id, mute);

    return new MuteOutcome.Muted(mute);
  }

  public boolean unmute(@NonNull UUID id) {
    var previous = this.cache.remove(id);
    if (previous == null) {
      return false;
    }

    Runnable persist = () -> this.store.delete(id);
    this.writer.submit("delete mute", persist);

    return true;
  }

  private void applyMute(@NonNull UUID id, @NonNull Mute mute) {
    this.cache.put(id, mute);

    Runnable persist = () -> this.store.save(id, mute);
    this.writer.submit("save mute", persist);
  }
}
