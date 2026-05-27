package com.hanielcota.essentials.modules.mute.service;

import io.github.hanielcota.commandframework.core.util.TimeParser;
import java.time.Duration;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Parses raw duration strings into {@link Duration} instances. Extracted from {@link MuteService}
 * so input parsing is a separate concern from mute lifecycle management.
 */
public final class MuteDurationParser {

  private MuteDurationParser() {}

  public static @Nullable Duration tryParse(@NonNull String input) {
    try {
      return TimeParser.parse(input);
    } catch (IllegalArgumentException ignored) {
      return null;
    }
  }
}
