package com.hanielcota.essentials.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, staticName = "of")
public final class Log {

  private final Logger jul;

  public static Log of(@NonNull Class<?> owner) {
    var loggerName = owner.getName();
    var javaLogger = Logger.getLogger(loggerName);

    return of(javaLogger);
  }

  private static String format(@NonNull String pattern, @NonNull Object[] args) {
    if (args.length == 0) {
      return pattern;
    }

    var patternLength = pattern.length();
    var capacity = patternLength + (args.length * 16);
    var sb = new StringBuilder(capacity);

    var argIdx = 0;
    var pos = 0;

    while (pos < patternLength) {
      var next = pattern.indexOf("{}", pos);

      if (next < 0 || argIdx >= args.length) {
        sb.append(pattern, pos, patternLength);
        return sb.toString();
      }

      sb.append(pattern, pos, next);
      sb.append(args[argIdx++]);
      pos = next + 2;
    }

    return sb.toString();
  }

  public void info(@NonNull String pattern, @NonNull Object... args) {
    if (this.jul.isLoggable(Level.INFO)) {
      var formattedMessage = format(pattern, args);
      this.jul.info(formattedMessage);
    }
  }

  public void warn(@NonNull String pattern, @NonNull Object... args) {
    if (this.jul.isLoggable(Level.WARNING)) {
      var formattedMessage = format(pattern, args);
      this.jul.warning(formattedMessage);
    }
  }

  public void warn(@NonNull Throwable thrown, @NonNull String pattern, @NonNull Object... args) {
    if (this.jul.isLoggable(Level.WARNING)) {
      var formattedMessage = format(pattern, args);
      this.jul.log(Level.WARNING, formattedMessage, thrown);
    }
  }

  public void error(@NonNull String pattern, @NonNull Object... args) {
    if (this.jul.isLoggable(Level.SEVERE)) {
      var formattedMessage = format(pattern, args);
      this.jul.severe(formattedMessage);
    }
  }

  public void error(@NonNull Throwable thrown, @NonNull String pattern, @NonNull Object... args) {
    if (this.jul.isLoggable(Level.SEVERE)) {
      var formattedMessage = format(pattern, args);
      this.jul.log(Level.SEVERE, formattedMessage, thrown);
    }
  }
}
