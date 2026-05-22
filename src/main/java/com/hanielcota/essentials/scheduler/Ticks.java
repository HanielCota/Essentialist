package com.hanielcota.essentials.scheduler;

import java.time.Duration;
import java.util.Objects;

/** Converts {@link Duration} values into Minecraft server ticks. */
final class Ticks {

  private static final long MILLIS_PER_TICK = 50L;

  private Ticks() {}

  /**
   * Converts {@code duration} into ticks, clamped to a minimum of 1. Folia's region scheduler
   * rejects a delay or period of 0, so any sub-tick duration is rounded up.
   */
  static long fromDuration(Duration duration) {
    Objects.requireNonNull(duration, "duration");

    return Math.max(1L, duration.toMillis() / MILLIS_PER_TICK);
  }
}
