package com.hanielcota.essentials.config;

import java.util.function.BiFunction;

/**
 * Utility for message configs that follow the self/other pattern.
 *
 * <p>Eliminates the repetitive {@code xxxFor(boolean, ...)} methods by providing a single {@link
 * #apply(boolean, String, BiFunction)} call.
 */
public final class TargetedMessageConfig {

  private TargetedMessageConfig() {}

  /**
   * Formats a self/other message pair.
   *
   * @param selfTarget whether the target is the actor themselves
   * @param playerName the target player's name (ignored when selfTarget is true)
   * @param formatter receives {@code (template, playerName)} and returns the formatted string
   * @return the formatted message for the actor
   */
  public static String format(
      boolean selfTarget, String playerName, BiFunction<String, String, String> formatter) {
    return selfTarget ? formatter.apply("self", "") : formatter.apply("other", playerName);
  }
}
