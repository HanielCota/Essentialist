package com.hanielcota.essentials.modules.kit.service;

import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Formats a remaining cooldown in seconds as a compact {@code 1h 2m 3s} string. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KitDurations {

  private static final long SECONDS_PER_HOUR = 3600L;
  private static final long SECONDS_PER_MINUTE = 60L;

  public static String format(long totalSeconds) {
    if (totalSeconds <= 0) {
      return "0s";
    }

    var hours = totalSeconds / SECONDS_PER_HOUR;
    var minutes = (totalSeconds % SECONDS_PER_HOUR) / SECONDS_PER_MINUTE;
    var seconds = totalSeconds % SECONDS_PER_MINUTE;

    var parts = new ArrayList<String>(3);
    if (hours > 0) {
      parts.add(hours + "h");
    }
    if (minutes > 0) {
      parts.add(minutes + "m");
    }
    if (seconds > 0) {
      parts.add(seconds + "s");
    }

    return String.join(" ", parts);
  }
}
