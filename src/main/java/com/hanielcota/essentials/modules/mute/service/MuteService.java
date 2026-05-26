package com.hanielcota.essentials.modules.mute.service;

import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.modules.mute.domain.MuteOutcome;
import com.hanielcota.essentials.modules.mute.repository.MuteCache;
import io.github.hanielcota.commandframework.core.util.TimeParser;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Application service for the mute use cases. Owns the {@code essentials.mute.exempt} permission
 * check, duration parsing and {@link Mute} domain-object factory so commands stay thin.
 *
 * <p>In-memory caching is delegated to {@link MuteCache} which handles cache eviction and async
 * persistence coordination.
 */
public final class MuteService {

  public static final String EXEMPT_PERMISSION = "essentials.mute.exempt";

  private final MuteCache cache;

  public MuteService(@NonNull MuteCache cache) {
    this.cache = cache;
  }

  private static @Nullable Duration tryParseDuration(@NonNull String input) {
    try {
      return TimeParser.parse(input);
    } catch (IllegalArgumentException ignored) {
      return null;
    }
  }

  public Optional<Mute> activeMute(@NonNull UUID id) {
    return this.cache.activeMute(id);
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
    this.cache.apply(id, mute);

    return new MuteOutcome.Muted(mute);
  }

  public boolean unmute(@NonNull UUID id) {
    return this.cache.remove(id);
  }
}
