package com.hanielcota.essentials.shared;

import java.time.Duration;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DurationFormatter {

  private static final String SUFFIX_DAYS = "d";
  private static final String SUFFIX_HOURS = "h";
  private static final String SUFFIX_MINUTES = "m";
  private static final String SUFFIX_SECONDS = "s";

  public static String format(@NonNull Duration duration) {
    return format(duration, SUFFIX_DAYS, SUFFIX_HOURS, SUFFIX_MINUTES, SUFFIX_SECONDS);
  }

  public static String format(
      @NonNull Duration duration,
      @NonNull String daySuffix,
      @NonNull String hourSuffix,
      @NonNull String minuteSuffix,
      @NonNull String secondSuffix) {
    var totalSeconds = Math.max(0L, duration.toSeconds());

    var days = totalSeconds / 86_400L;
    var hours = (totalSeconds % 86_400L) / 3_600L;
    var minutes = (totalSeconds % 3_600L) / 60L;
    var seconds = totalSeconds % 60L;

    var parts = new ArrayList<String>();

    if (days > 0) {
      parts.add(days + daySuffix);
    }

    if (hours > 0) {
      parts.add(hours + hourSuffix);
    }

    if (minutes > 0) {
      parts.add(minutes + minuteSuffix);
    }

    if (seconds > 0 || parts.isEmpty()) {
      parts.add(seconds + secondSuffix);
    }

    return String.join(" ", parts);
  }
}
