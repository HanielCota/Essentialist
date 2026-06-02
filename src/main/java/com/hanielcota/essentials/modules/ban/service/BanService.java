package com.hanielcota.essentials.modules.ban.service;

import com.hanielcota.essentials.modules.ban.domain.ActiveBan;
import com.hanielcota.essentials.modules.ban.domain.Ban;
import com.hanielcota.essentials.modules.ban.repository.BanRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * Application service for the ban use cases. Owns the {@link Ban} factory and delegates
 * lookup/persistence to {@link BanRepository} so the cache split stays invisible to callers.
 * Permission and exemption checks belong to the caller (the menu flow), not here.
 */
@RequiredArgsConstructor
public final class BanService {

  private final @NonNull BanRepository repository;

  public Optional<Ban> activeBan(@NonNull UUID id) {
    return this.repository.findActive(id, Instant.now());
  }

  public boolean isBanned(@NonNull UUID id) {
    return activeBan(id).isPresent();
  }

  public List<ActiveBan> listActive() {
    return this.repository.listActive(Instant.now());
  }

  /**
   * Applies a ban to {@code id}. A {@code null} duration produces a permanent ban. Returns the
   * stored {@link Ban} so the caller can render the outcome and kick the target.
   */
  public Ban ban(
      @NonNull UUID id,
      @NonNull String name,
      @Nullable Duration duration,
      @NonNull String reason,
      @NonNull String issuer) {
    var now = Instant.now();
    var ban = Ban.from(duration, reason, issuer, now);
    var entry = new ActiveBan(id, name, ban);

    this.repository.save(entry);

    return ban;
  }

  public boolean unban(@NonNull UUID id) {
    return this.repository.delete(id);
  }
}
