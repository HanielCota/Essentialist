package com.hanielcota.essentials.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Performs {@code {}} token replacement for log message patterns. Separated from {@link Log} so the
 * formatter can be tested and evolved independently of the logging backend.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MessageFormatter {

  static String format(@NonNull String pattern, @NonNull Object[] args) {
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
}
