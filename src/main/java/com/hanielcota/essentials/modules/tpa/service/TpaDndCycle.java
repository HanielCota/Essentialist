package com.hanielcota.essentials.modules.tpa.service;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Cycles DND timestamps through a fixed preset wheel:
 *
 * <pre>Off → 30 minutes → 1 hour → 4 hours → Off</pre>
 *
 * <p>Stage is inferred from how much time is left on the current DND, so the helper is stateless
 * even though {@link TpaProfile#dndUntilEpochMs()} is a single timestamp.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TpaDndCycle {

  public static Stage stageOf(long dndUntilEpochMs, long nowEpochMs) {
    var remainingMs = dndUntilEpochMs - nowEpochMs;
    if (remainingMs <= 0) {
      return Stage.OFF;
    }
    if (remainingMs <= Duration.ofMinutes(30).toMillis()) {
      return Stage.THIRTY_MINUTES;
    }
    if (remainingMs <= Duration.ofHours(1).toMillis()) {
      return Stage.ONE_HOUR;
    }
    return Stage.FOUR_HOURS;
  }

  public static long cycleTo(@NonNull Stage next, long nowEpochMs) {
    if (next == Stage.OFF) {
      return 0L;
    }
    return nowEpochMs + next.duration().toMillis();
  }

  public enum Stage {
    OFF,
    THIRTY_MINUTES,
    ONE_HOUR,
    FOUR_HOURS;

    public Stage next() {
      return switch (this) {
        case OFF -> THIRTY_MINUTES;
        case THIRTY_MINUTES -> ONE_HOUR;
        case ONE_HOUR -> FOUR_HOURS;
        case FOUR_HOURS -> OFF;
      };
    }

    public Duration duration() {
      return switch (this) {
        case OFF -> Duration.ZERO;
        case THIRTY_MINUTES -> Duration.ofMinutes(30);
        case ONE_HOUR -> Duration.ofHours(1);
        case FOUR_HOURS -> Duration.ofHours(4);
      };
    }
  }
}
