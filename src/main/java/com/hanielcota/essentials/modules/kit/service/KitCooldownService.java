package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.modules.kit.domain.Kit;
import com.hanielcota.essentials.modules.kit.repository.KitUsageRepository;
import java.util.UUID;
import java.util.function.LongSupplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Cooldown and one-time bookkeeping over {@link KitUsageRepository}. Time comes from an injected
 * {@link LongSupplier} (epoch millis) so the windows are unit-testable without a real clock.
 */
@RequiredArgsConstructor
public final class KitCooldownService {

  private static final long MILLIS_PER_SECOND = 1000L;

  private final KitUsageRepository usage;
  private final LongSupplier clock;

  /** Whether {@code player} has ever claimed {@code kit} (the gate for one-time kits). */
  public boolean hasClaimed(@NonNull UUID player, @NonNull Kit kit) {
    var history = this.usage.findAll(player);

    return history.containsKey(kit.id());
  }

  /** Seconds left on {@code kit}'s cooldown for {@code player}; {@code 0} means ready. */
  public long remainingSeconds(@NonNull UUID player, @NonNull Kit kit) {
    if (!kit.hasCooldown()) {
      return 0;
    }

    var history = this.usage.findAll(player);
    var lastUsed = history.get(kit.id());
    if (lastUsed == null) {
      return 0;
    }

    var windowMs = kit.cooldownSeconds() * MILLIS_PER_SECOND;
    var elapsedMs = this.clock.getAsLong() - lastUsed;
    var remainingMs = windowMs - elapsedMs;
    if (remainingMs <= 0) {
      return 0;
    }

    return ceilToSeconds(remainingMs);
  }

  /** Records {@code player} claiming {@code kit} now. */
  public void markClaimed(@NonNull UUID player, @NonNull Kit kit) {
    var now = this.clock.getAsLong();
    var kitId = kit.id();

    this.usage.upsert(player, kitId, now);
  }

  private static long ceilToSeconds(long millis) {
    return (millis + MILLIS_PER_SECOND - 1) / MILLIS_PER_SECOND;
  }
}
