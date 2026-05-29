package com.hanielcota.essentials.modules.kit.service;

import java.util.ArrayList;
import java.util.Locale;
import java.util.OptionalLong;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Parses and formats kit cooldown durations ({@code 1d2h30m}, {@code 90s} or raw seconds). */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KitDurations {

  private static final long SECONDS_PER_MINUTE = 60L;
  private static final long SECONDS_PER_HOUR = 3600L;
  private static final long SECONDS_PER_DAY = 86_400L;

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

  /**
   * Parses a duration into seconds. Accepts raw seconds ({@code 90}) or unit groups in any
   * combination ({@code 1d2h30m10s}); commas and spaces between groups are ignored. Returns an
   * empty result when the input is malformed.
   *
   * <p>Hand-scanned rather than regex-based so it stays linear-time on any input (no backtracking).
   */
  public static OptionalLong parseSeconds(@NonNull String input) {
    var normalized = input.trim().toLowerCase(Locale.ROOT).replace(",", "").replace(" ", "");
    if (normalized.isEmpty()) {
      return OptionalLong.empty();
    }

    var total = 0L;
    var current = 0L;
    var hasDigit = false;
    var sawUnit = false;

    for (var index = 0; index < normalized.length(); index++) {
      var ch = normalized.charAt(index);

      if (ch >= '0' && ch <= '9') {
        current = current * 10 + (ch - '0');
        hasDigit = true;
        continue;
      }

      var unit = unitSeconds(ch);
      if (unit < 0 || !hasDigit) {
        return OptionalLong.empty();
      }

      total += current * unit;
      current = 0;
      hasDigit = false;
      sawUnit = true;
    }

    if (!sawUnit) {
      return hasDigit ? OptionalLong.of(current) : OptionalLong.empty();
    }

    // A trailing number with no unit (e.g. "1d2") is malformed.
    return hasDigit ? OptionalLong.empty() : OptionalLong.of(total);
  }

  private static long unitSeconds(char unit) {
    return switch (unit) {
      case 'd' -> SECONDS_PER_DAY;
      case 'h' -> SECONDS_PER_HOUR;
      case 'm' -> SECONDS_PER_MINUTE;
      case 's' -> 1L;
      default -> -1L;
    };
  }
}
