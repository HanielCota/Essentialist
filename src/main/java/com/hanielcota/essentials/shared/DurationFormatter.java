package com.hanielcota.essentials.shared;

import java.time.Duration;
import java.util.ArrayList;
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

    var parts = new ArrayList<String>();

    if (days > 0) {
      parts.add(days + "d");
    }

    if (hours > 0) {
      parts.add(hours + "h");
    }

    if (minutes > 0) {
      parts.add(minutes + "m");
    }

    if (seconds > 0 || parts.isEmpty()) {
      parts.add(seconds + "s");
    }

    return String.join(" ", parts);
  }
}
