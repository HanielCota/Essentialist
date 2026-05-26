package com.hanielcota.essentials.util;

import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Reusable token-replacement for message templates that use {@code {key}} placeholders.
 *
 * <p>Callers pass alternating key-value pairs: {@code format("hello {player}", "player", name)}.
 * Config records that currently reimplement the same {@code .replace("{token}", value)} chain
 * manually can delegate to this utility.
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class TemplateFormatter {

  public static String format(@NonNull String template, @NonNull String... keyValues) {
    var result = template;
    for (var i = 0; i < keyValues.length; i += 2) {
      result = result.replace("{" + keyValues[i] + "}", keyValues[i + 1]);
    }
    return result;
  }
}
