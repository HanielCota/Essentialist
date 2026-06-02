package com.hanielcota.essentials.modules.ban.service;

import io.github.hanielcota.commandframework.core.util.TimeParser;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Parses a ban duration string into a {@link Duration}. A blank string is a permanent ban and
 * yields {@code null}; an unparseable string also yields {@code null} (the menu only feeds it
 * configured values, so this is a defensive fallback).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BanDurationParser {

  public static @Nullable Duration tryParse(@NonNull String input) {
    if (input.isBlank()) {
      return null;
    }

    try {
      return TimeParser.parse(input);
    } catch (IllegalArgumentException ignored) {
      return null;
    }
  }
}
