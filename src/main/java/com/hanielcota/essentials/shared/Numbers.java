package com.hanielcota.essentials.shared;

import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Numbers {

  public static String display(double value) {
    if (!Double.isFinite(value)) {
      return Double.toString(value);
    }
    if (value == Math.floor(value)) {
      // (double) Long.MAX_VALUE rounds up to 2^63, which overflows the long cast, so the upper edge
      // must be excluded with >=. Whole doubles inside [-2^63, 2^63) cast to long losslessly.
      if (value < (double) Long.MIN_VALUE || value >= (double) Long.MAX_VALUE) {
        return Double.toString(value);
      }
      var longValue = (long) value;
      return Long.toString(longValue);
    }

    return String.format(Locale.ROOT, "%.2f", value);
  }
}
