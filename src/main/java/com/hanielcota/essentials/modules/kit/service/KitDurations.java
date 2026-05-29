package com.hanielcota.essentials.modules.kit.service;

import java.util.ArrayList;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/** Parses and formats kit cooldown durations ({@code 1d2h30m}, {@code 90s} or raw seconds). */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KitDurations {

  private static final long SECONDS_PER_MINUTE = 60L;
  private static final long SECONDS_PER_HOUR = 3600L;
  private static final long SECONDS_PER_DAY = 86_400L;

  private static final Pattern RAW_SECONDS = Pattern.compile("\\d+");
  private static final Pattern UNIT = Pattern.compile("(\\d+)([dhms])");

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
   */
  public static OptionalLong parseSeconds(@NonNull String input) {
    var normalized = input.trim().toLowerCase(Locale.ROOT).replace(",", "").replace(" ", "");
    if (normalized.isEmpty()) {
      return OptionalLong.empty();
    }

    if (RAW_SECONDS.matcher(normalized).matches()) {
      return parseLong(normalized);
    }

    return parseUnits(normalized);
  }

  private static OptionalLong parseUnits(@NonNull String normalized) {
    var matcher = UNIT.matcher(normalized);

    var total = 0L;
    var covered = 0;
    var matched = false;
    while (matcher.find()) {
      if (matcher.start() != covered) {
        return OptionalLong.empty();
      }

      var amount = parseLong(matcher.group(1));
      if (amount.isEmpty()) {
        return OptionalLong.empty();
      }

      matched = true;
      total += amount.getAsLong() * unitSeconds(matcher.group(2).charAt(0));
      covered = matcher.end();
    }

    if (!matched || covered != normalized.length()) {
      return OptionalLong.empty();
    }

    return OptionalLong.of(total);
  }

  private static long unitSeconds(char unit) {
    return switch (unit) {
      case 'd' -> SECONDS_PER_DAY;
      case 'h' -> SECONDS_PER_HOUR;
      case 'm' -> SECONDS_PER_MINUTE;
      default -> 1L;
    };
  }

  private static OptionalLong parseLong(@NonNull String digits) {
    try {
      return OptionalLong.of(Long.parseLong(digits));
    } catch (NumberFormatException e) {
      return OptionalLong.empty();
    }
  }
}
