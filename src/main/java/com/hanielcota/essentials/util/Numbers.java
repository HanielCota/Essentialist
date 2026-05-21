package com.hanielcota.essentials.util;

import java.util.Locale;

public final class Numbers {

  private Numbers() {}

  public static String compact(double n) {
    return n == Math.floor(n) ? Long.toString((long) n) : String.format(Locale.ROOT, "%.2f", n);
  }
}
