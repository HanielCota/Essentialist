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
      if (value < Long.MIN_VALUE || value > Long.MAX_VALUE) {
        return Double.toString(value);
      }
      var longValue = (long) value;
      return Long.toString(longValue);
    }

    return String.format(Locale.ROOT, "%.2f", value);
  }
}
