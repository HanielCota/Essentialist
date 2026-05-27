package com.hanielcota.essentials.modules.tpa.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Per-sender rate limit on {@link com.hanielcota.essentials.modules.tpa.command.TpaSendOrchestrator
 * TpaSendOrchestrator.send}. Lives outside the command framework's annotation-driven
 * {@code @Cooldown} because every menu and click handler in this module reaches the same
 * orchestrator, and the annotation only covers the command entry points — so a player could spam
 * {@code /tpa Foo} once, then keep clicking the target-action menu to bypass the cooldown.
 *
 * <p>The state is intentionally session-scoped: when a player disconnects we do not clear the map.
 * A repeat join still has to wait, which is the desired behavior. Entries are tiny ({@code Instant}
 * per UUID); no GC is needed at the scale of a game server.
 */
public final class TpaSendRateLimiter {

  private final Map<UUID, Instant> lastSend = new ConcurrentHashMap<>();

  /**
   * Tries to record a send for {@code sender}. Returns {@code true} when the cooldown has elapsed
   * (or this is the first send) and the new timestamp is stored; {@code false} otherwise. Atomic
   * per-key via {@link Map#compute(Object, java.util.function.BiFunction)}.
   */
  public boolean tryClaim(@NonNull UUID sender, @NonNull Duration cooldown) {
    var now = Instant.now();
    var claimed = new boolean[1];

    this.lastSend.compute(
        sender,
        (key, prior) -> {
          var blocked = prior != null && prior.plus(cooldown).isAfter(now);
          if (blocked) {
            claimed[0] = false;
            return prior;
          }
          claimed[0] = true;
          return now;
        });

    return claimed[0];
  }

  /** Seconds left before {@code sender} can claim again, rounded up. Zero when ready. */
  public long remainingSeconds(@NonNull UUID sender, @NonNull Duration cooldown) {
    var prior = this.lastSend.get(sender);
    if (prior == null) {
      return 0L;
    }

    var deadline = prior.plus(cooldown);
    var now = Instant.now();
    if (!now.isBefore(deadline)) {
      return 0L;
    }

    var remaining = Duration.between(now, deadline);
    var seconds = remaining.toSeconds();
    if (remaining.minusSeconds(seconds).isZero()) {
      return seconds;
    }
    return seconds + 1;
  }
}
