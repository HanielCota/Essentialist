package com.hanielcota.essentials.util;

import java.time.Duration;
import java.util.Objects;

public final class DurationFormatter {

  private DurationFormatter() {}

  public static String format(Duration duration) {
    Objects.requireNonNull(duration, "duration");
    long totalSeconds = Math.max(0L, duration.toSeconds());

    long days = totalSeconds / 86_400L;
    long hours = (totalSeconds % 86_400L) / 3_600L;
    long minutes = (totalSeconds % 3_600L) / 60L;
    long seconds = totalSeconds % 60L;

    StringBuilder sb = new StringBuilder();
    if (days > 0) sb.append(days).append("d ");
    if (hours > 0) sb.append(hours).append("h ");
    if (minutes > 0) sb.append(minutes).append("m ");
    if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");

    return sb.toString().trim();
  }
}
