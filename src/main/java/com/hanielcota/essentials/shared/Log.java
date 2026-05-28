package com.hanielcota.essentials.shared;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Lightweight logger abstraction backed by {@code java.util.logging}. The {@link MessageFormatter}
 * handles {@code {}} token replacement separately so this class stays focused on level-checking and
 * delegation.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Log {

  private final java.util.logging.Logger logger;

  public static Log of(@NonNull Class<?> owner) {
    var loggerName = owner.getName();
    var javaLogger = java.util.logging.Logger.getLogger(loggerName);

    return new Log(javaLogger);
  }

  public void info(@NonNull String pattern, @NonNull Object... args) {
    if (this.logger.isLoggable(java.util.logging.Level.INFO)) {
      var formattedMessage = MessageFormatter.format(pattern, args);
      this.logger.info(formattedMessage);
    }
  }

  public void warn(@NonNull String pattern, @NonNull Object... args) {
    if (this.logger.isLoggable(java.util.logging.Level.WARNING)) {
      var formattedMessage = MessageFormatter.format(pattern, args);
      this.logger.warning(formattedMessage);
    }
  }

  public void warn(@NonNull Throwable thrown, @NonNull String pattern, @NonNull Object... args) {
    if (this.logger.isLoggable(java.util.logging.Level.WARNING)) {
      var formattedMessage = MessageFormatter.format(pattern, args);
      this.logger.log(java.util.logging.Level.WARNING, formattedMessage, thrown);
    }
  }

  public void error(@NonNull String pattern, @NonNull Object... args) {
    if (this.logger.isLoggable(java.util.logging.Level.SEVERE)) {
      var formattedMessage = MessageFormatter.format(pattern, args);
      this.logger.severe(formattedMessage);
    }
  }

  public void error(@NonNull Throwable thrown, @NonNull String pattern, @NonNull Object... args) {
    if (this.logger.isLoggable(java.util.logging.Level.SEVERE)) {
      var formattedMessage = MessageFormatter.format(pattern, args);
      this.logger.log(java.util.logging.Level.SEVERE, formattedMessage, thrown);
    }
  }
}
