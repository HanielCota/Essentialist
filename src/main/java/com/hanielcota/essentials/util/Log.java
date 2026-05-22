package com.hanielcota.essentials.util;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Log {

  private final Logger jul;

  private Log(Logger jul) {
    this.jul = jul;
  }

  public static Log of(Class<?> owner) {
    Objects.requireNonNull(owner, "owner");
    return new Log(Logger.getLogger(owner.getName()));
  }

  private static String format(String pattern, Object[] args) {
    if (args.length == 0) {
      return pattern;
    }
    var sb = new StringBuilder(pattern.length() + args.length * 16);
    int argIdx = 0;
    int pos = 0;
    while (pos < pattern.length()) {
      int next = pattern.indexOf("{}", pos);
      if (next < 0 || argIdx >= args.length) {
        sb.append(pattern, pos, pattern.length());
        return sb.toString();
      }
      sb.append(pattern, pos, next);
      sb.append(args[argIdx++]);
      pos = next + 2;
    }
    return sb.toString();
  }

  public void info(String pattern, Object... args) {
    if (jul.isLoggable(Level.INFO)) {
      jul.info(format(pattern, args));
    }
  }

  public void warn(String pattern, Object... args) {
    if (jul.isLoggable(Level.WARNING)) {
      jul.warning(format(pattern, args));
    }
  }

  public void warn(Throwable thrown, String pattern, Object... args) {
    if (jul.isLoggable(Level.WARNING)) {
      jul.log(Level.WARNING, format(pattern, args), thrown);
    }
  }

  public void error(String pattern, Object... args) {
    if (jul.isLoggable(Level.SEVERE)) {
      jul.severe(format(pattern, args));
    }
  }

  public void error(Throwable thrown, String pattern, Object... args) {
    if (jul.isLoggable(Level.SEVERE)) {
      jul.log(Level.SEVERE, format(pattern, args), thrown);
    }
  }
}
