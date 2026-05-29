package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.modules.kit.domain.Kit;
import com.hanielcota.essentials.modules.kit.repository.KitUsageRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Cooldown and one-time bookkeeping over {@link KitUsageRepository}. Time comes from an injected
 * {@link LongSupplier} (epoch millis), the daily-reset hour from an {@link IntSupplier} and the
 * zone from {@link ZoneId}, so both the rolling and the daily windows are unit-testable.
 */
@RequiredArgsConstructor
public final class KitCooldownService {

  private static final long MILLIS_PER_SECOND = 1000L;
  private static final long MILLIS_PER_DAY = 86_400_000L;
  private static final int MIN_HOUR = 0;
  private static final int MAX_HOUR = 23;

  private final KitUsageRepository usage;
  private final LongSupplier clock;
  private final IntSupplier dailyResetHour;
  private final ZoneId zone;

  /** Whether {@code player} has ever claimed {@code kit} (the gate for one-time kits). */
  public boolean hasClaimed(@NonNull UUID player, @NonNull Kit kit) {
    var history = this.usage.findAll(player);

    return history.containsKey(kit.id());
  }

  /** Seconds left on {@code kit}'s cooldown for {@code player}; {@code 0} means ready. */
  public long remainingSeconds(@NonNull UUID player, @NonNull Kit kit) {
    if (!kit.hasCooldownGate()) {
      return 0;
    }

    var history = this.usage.findAll(player);
    var lastUsed = history.get(kit.id());
    if (lastUsed == null) {
      return 0;
    }

    var now = this.clock.getAsLong();
    if (kit.dailyReset()) {
      return dailyRemaining(lastUsed, now);
    }

    return rollingRemaining(lastUsed, now, kit.cooldownSeconds());
  }

  /** Records {@code player} claiming {@code kit} now. */
  public void markClaimed(@NonNull UUID player, @NonNull Kit kit) {
    var now = this.clock.getAsLong();
    var kitId = kit.id();

    this.usage.upsert(player, kitId, now);
  }

  private static long rollingRemaining(long lastUsed, long now, long cooldownSeconds) {
    var windowMs = cooldownSeconds * MILLIS_PER_SECOND;
    var remainingMs = windowMs - (now - lastUsed);

    return remainingMs <= 0 ? 0 : ceilToSeconds(remainingMs);
  }

  private long dailyRemaining(long lastUsed, long now) {
    var boundary = lastResetBoundaryMs(now);
    if (lastUsed < boundary) {
      return 0;
    }

    var nextBoundary = boundary + MILLIS_PER_DAY;
    return ceilToSeconds(nextBoundary - now);
  }

  // The most recent daily-reset instant at or before {@code now}, in epoch millis.
  private long lastResetBoundaryMs(long now) {
    var hour = Math.clamp(this.dailyResetHour.getAsInt(), MIN_HOUR, MAX_HOUR);

    var today = Instant.ofEpochMilli(now).atZone(this.zone).toLocalDate();
    var todayReset = today.atTime(hour, 0).atZone(this.zone).toInstant().toEpochMilli();

    return todayReset <= now ? todayReset : todayReset - MILLIS_PER_DAY;
  }

  private static long ceilToSeconds(long millis) {
    return (millis + MILLIS_PER_SECOND - 1) / MILLIS_PER_SECOND;
  }
}
