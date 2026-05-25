package com.hanielcota.essentials.modules.nick.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Validates nickname strings. Accepts only Minecraft-name-compatible characters (alphanumeric +
 * underscore) within the caller-supplied length bounds. Color codes / MiniMessage tags are
 * intentionally rejected for now — they complicate reverse lookups and impersonation checks.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NickValidator {

  public static Result check(@NonNull String nickname, int minLength, int maxLength) {
    var length = nickname.length();

    if (length < minLength) {
      return Result.TOO_SHORT;
    }

    if (length > maxLength) {
      return Result.TOO_LONG;
    }

    if (!isAllowed(nickname)) {
      return Result.INVALID_CHARS;
    }

    return Result.OK;
  }

  private static boolean isAllowed(@NonNull String nickname) {
    for (var i = 0; i < nickname.length(); i++) {
      var ch = nickname.charAt(i);
      if (!isAllowedChar(ch)) {
        return false;
      }
    }

    return true;
  }

  private static boolean isAllowedChar(char ch) {
    if (ch >= 'a' && ch <= 'z') {
      return true;
    }
    if (ch >= 'A' && ch <= 'Z') {
      return true;
    }
    if (ch >= '0' && ch <= '9') {
      return true;
    }

    return ch == '_';
  }

  public enum Result {
    OK,
    TOO_SHORT,
    TOO_LONG,
    INVALID_CHARS
  }
}
