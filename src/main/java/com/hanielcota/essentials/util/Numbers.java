package com.hanielcota.essentials.util;

import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Numbers {

  public static String compact(double n) {
    if (!Double.isFinite(n)) {
      // NaN and ±Infinity would round-trip through (long) as 0 / Long.MAX_VALUE /
      // Long.MIN_VALUE and render as an absurd integer. Surface them as-is.
      return Double.toString(n);
    }
    if (n == Math.floor(n)) {
      // Integer-valued doubles outside the long range overflow the cast and would render as
      // Long.MAX_VALUE / Long.MIN_VALUE. Fall back to scientific notation for those.
      if (n < Long.MIN_VALUE || n > Long.MAX_VALUE) {
        return Double.toString(n);
      }
      var longValue = (long) n;
      return Long.toString(longValue);
    }

    return String.format(Locale.ROOT, "%.2f", n);
  }
}
