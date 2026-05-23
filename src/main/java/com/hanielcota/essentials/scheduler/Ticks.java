package com.hanielcota.essentials.scheduler;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Converts {@link Duration} values into Minecraft server ticks. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class Ticks {

  private static final long MILLIS_PER_TICK = 50L;

  /**
   * Converts {@code duration} into ticks, clamped to a minimum of 1. Folia's region scheduler
   * rejects a delay or period of 0, so any sub-tick duration is rounded up.
   */
  static long fromDuration(Duration duration) {
    return Math.max(1L, duration.toMillis() / MILLIS_PER_TICK);
  }
}
