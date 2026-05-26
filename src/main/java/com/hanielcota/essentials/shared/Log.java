package com.hanielcota.essentials.shared;

import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE, staticName = "of")
public final class Log {

  private final Logger logger;

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
    var builder = new StringBuilder(capacity);

    var argIndex = 0;
    var cursor = 0;

    while (cursor < patternLength) {
      var tokenStart = pattern.indexOf("{}", cursor);

      if (tokenStart < 0 || argIndex >= args.length) {
        builder.append(pattern, cursor, patternLength);
        return builder.toString();
      }

      builder.append(pattern, cursor, tokenStart);
      builder.append(args[argIndex++]);
      cursor = tokenStart + 2;
    }

    return builder.toString();
  }

  public void info(@NonNull String pattern, @NonNull Object... args) {
    if (this.logger.isLoggable(Level.INFO)) {
      var formattedMessage = format(pattern, args);
      this.logger.info(formattedMessage);
    }
  }

  public void warn(@NonNull String pattern, @NonNull Object... args) {
    if (this.logger.isLoggable(Level.WARNING)) {
      var formattedMessage = format(pattern, args);
      this.logger.warning(formattedMessage);
    }
  }

  public void warn(@NonNull Throwable thrown, @NonNull String pattern, @NonNull Object... args) {
    if (this.logger.isLoggable(Level.WARNING)) {
      var formattedMessage = format(pattern, args);
      this.logger.log(Level.WARNING, formattedMessage, thrown);
    }
  }

  public void error(@NonNull String pattern, @NonNull Object... args) {
    if (this.logger.isLoggable(Level.SEVERE)) {
      var formattedMessage = format(pattern, args);
      this.logger.severe(formattedMessage);
    }
  }

  public void error(@NonNull Throwable thrown, @NonNull String pattern, @NonNull Object... args) {
    if (this.logger.isLoggable(Level.SEVERE)) {
      var formattedMessage = format(pattern, args);
      this.logger.log(Level.SEVERE, formattedMessage, thrown);
    }
  }
}
