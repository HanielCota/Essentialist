package com.hanielcota.essentials.modules.mute.service;

import com.hanielcota.essentials.api.MutesApi;
import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.modules.mute.domain.MuteOutcome;
import com.hanielcota.essentials.modules.mute.repository.MuteRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Application service for the mute use cases. Owns the {@link Mute} domain-object factory and
 * delegates lookup/persistence to {@link MuteRepository} so the cache split stays invisible to
 * callers.
 */
@RequiredArgsConstructor
public final class MuteService implements MutesApi {

  private final @NonNull MuteRepository repository;

  public Optional<Mute> activeMute(@NonNull UUID id) {
    return this.repository.findActive(id, Instant.now());
  }

  @Override
  public boolean isMuted(@NonNull UUID id) {
    return activeMute(id).isPresent();
  }

  /**
   * Applies a mute to {@code target}. Returns {@link MuteOutcome.Muted} on success. An empty {@code
   * rawDuration} produces a permanent mute. The caller is responsible for checking permissions and
   * validating the duration format before calling this method.
   */
  public MuteOutcome mute(@NonNull Player target, @Nullable Duration duration) {
    var now = Instant.now();
    var mute = Mute.from(duration, now);
    var id = target.getUniqueId();
    this.repository.save(id, mute);

    return new MuteOutcome.Muted(mute);
  }

  public boolean unmute(@NonNull UUID id) {
    return this.repository.delete(id);
  }
}
