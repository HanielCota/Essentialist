package com.hanielcota.essentials.util;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DurationFormatter {

  public static String format(@NonNull Duration duration) {
    var totalSeconds = Math.max(0L, duration.toSeconds());

    var days = totalSeconds / 86_400L;
    var hours = (totalSeconds % 86_400L) / 3_600L;
    var minutes = (totalSeconds % 3_600L) / 60L;
    var seconds = totalSeconds % 60L;

    var sb = new StringBuilder();

    if (days > 0) {
      sb.append(days).append("d ");
    }

    if (hours > 0) {
      sb.append(hours).append("h ");
    }

    if (minutes > 0) {
      sb.append(minutes).append("m ");
    }

    if (seconds > 0 || sb.isEmpty()) {
      sb.append(seconds).append("s");
    }

    return sb.toString().trim();
  }
}
