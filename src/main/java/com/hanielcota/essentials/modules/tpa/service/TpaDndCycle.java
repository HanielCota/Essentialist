package com.hanielcota.essentials.modules.tpa.service;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Cycles DND timestamps through a three-stage preset wheel:
 *
 * <pre>Off → Stage 1 → Stage 2 → Stage 3 → Off</pre>
 *
 * <p>The actual durations live in config (see {@code TpaBehaviorSettingsMenuConfig} stage duration
 * fields); callers pass a {@link Durations} bundle built from the snapshot they already hold so
 * this helper stays stateless and config-free.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TpaDndCycle {

  public static Stage stageOf(long dndUntilEpochMs, long nowEpochMs, @NonNull Durations durations) {
    var remainingMs = dndUntilEpochMs - nowEpochMs;
    if (remainingMs <= 0) {
      return Stage.OFF;
    }
    if (remainingMs <= durations.stage1().toMillis()) {
      return Stage.STAGE_1;
    }
    if (remainingMs <= durations.stage2().toMillis()) {
      return Stage.STAGE_2;
    }
    return Stage.STAGE_3;
  }

  public static long cycleTo(@NonNull Stage next, long nowEpochMs, @NonNull Durations durations) {
    if (next == Stage.OFF) {
      return 0L;
    }
    return nowEpochMs + durationOf(next, durations).toMillis();
  }

  private static Duration durationOf(@NonNull Stage stage, @NonNull Durations durations) {
    return switch (stage) {
      case OFF -> Duration.ZERO;
      case STAGE_1 -> durations.stage1();
      case STAGE_2 -> durations.stage2();
      case STAGE_3 -> durations.stage3();
    };
  }

  public enum Stage {
    OFF,
    STAGE_1,
    STAGE_2,
    STAGE_3;

    public Stage next() {
      return switch (this) {
        case OFF -> STAGE_1;
        case STAGE_1 -> STAGE_2;
        case STAGE_2 -> STAGE_3;
        case STAGE_3 -> OFF;
      };
    }
  }

  public record Durations(
      @NonNull Duration stage1, @NonNull Duration stage2, @NonNull Duration stage3) {

    public static Durations ofMinutes(int stage1Minutes, int stage2Minutes, int stage3Minutes) {
      return new Durations(
          Duration.ofMinutes(stage1Minutes),
          Duration.ofMinutes(stage2Minutes),
          Duration.ofMinutes(stage3Minutes));
    }
  }
}
