package com.hanielcota.essentials.util;

import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Numbers {

  public static String compact(double n) {
    if (n == Math.floor(n)) {
      var longValue = (long) n;
      return Long.toString(longValue);
    }

    return String.format(Locale.ROOT, "%.2f", n);
  }
}
