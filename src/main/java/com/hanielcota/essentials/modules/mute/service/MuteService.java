package com.hanielcota.essentials.modules.mute.service;

import com.hanielcota.essentials.api.MutesApi;
import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.modules.mute.domain.MuteOutcome;
import com.hanielcota.essentials.modules.mute.repository.MuteCache;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Application service for the mute use cases. Owns the {@link Mute} domain-object factory and cache
 * coordination so commands stay thin. Duration parsing is delegated to {@link MuteDurationParser}.
 * Permission checks are the caller's responsibility.
 *
 * <p>In-memory caching is delegated to {@link MuteCache} which handles cache eviction and async
 * persistence coordination.
 */
public final class MuteService implements MutesApi {

  private final MuteCache cache;

  public MuteService(@NonNull MuteCache cache) {
    this.cache = cache;
  }

  public Optional<Mute> activeMute(@NonNull UUID id) {
    return this.cache.activeMute(id);
  }

  @Override
  public boolean isMuted(@NonNull UUID id) {
    return this.cache.activeMute(id).isPresent();
  }

  /**
   * Applies a mute to {@code target}. Returns {@link MuteOutcome.Muted} on success. An empty {@code
   * rawDuration} produces a permanent mute. The caller is responsible for checking permissions and
   * validating the duration format before calling this method.
   */
  public MuteOutcome mute(@NonNull Player target, @NonNull java.time.Duration duration) {
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
