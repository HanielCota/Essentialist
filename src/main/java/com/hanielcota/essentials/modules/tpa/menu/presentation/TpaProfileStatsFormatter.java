package com.hanielcota.essentials.modules.tpa.menu.presentation;

import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Formats lifetime TPA stats (acceptance rate, average accept latency, most-contacted player) for
 * use as placeholders in chat lines and menu lore.
 *
 * <p>Outputs use the configured fallback labels when there is not enough data to compute a value.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TpaProfileStatsFormatter {

  public static String acceptRate(@NonNull TpaProfile profile, @NonNull String fallback) {
    var sent = profile.sentRequests();
    if (sent <= 0) {
      return fallback;
    }
    var accepted = profile.acceptedSent();
    var percent = Math.round((accepted * 1000.0) / sent) / 10.0;

    return formatPercent(percent);
  }

  public static String averageAccept(@NonNull TpaProfile profile, @NonNull String fallback) {
    var count = profile.acceptCount();
    if (count <= 0) {
      return fallback;
    }
    var avgMs = profile.totalAcceptLatencyMs() / count;
    var duration = Duration.ofMillis(avgMs);

    return humanizeDuration(duration);
  }

  public static String mostContactedName(@Nullable String name, @NonNull String fallback) {
    if (name == null || name.isBlank()) {
      return fallback;
    }
    return name;
  }

  private static String formatPercent(double percent) {
    if (percent % 1.0 == 0.0) {
      var asInt = (long) percent;
      return asInt + "%";
    }
    return String.format(java.util.Locale.ROOT, "%.1f%%", percent);
  }

  private static String humanizeDuration(@NonNull Duration duration) {
    var totalSeconds = duration.toSeconds();
    if (totalSeconds < 60) {
      return totalSeconds + "s";
    }

    var minutes = totalSeconds / 60;
    var seconds = totalSeconds % 60;
    if (minutes < 60) {
      if (seconds == 0) {
        return minutes + "m";
      }
      return minutes + "m" + seconds + "s";
    }

    var hours = minutes / 60;
    var remainingMinutes = minutes % 60;
    if (remainingMinutes == 0) {
      return hours + "h";
    }
    return hours + "h" + remainingMinutes + "m";
  }
}
