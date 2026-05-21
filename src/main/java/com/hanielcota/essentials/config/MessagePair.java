package com.hanielcota.essentials.config;

import java.util.Objects;

/**
 * Immutable pair of self / other message templates.
 *
 * <p>Eliminates repetitive {@code xxxFor(boolean selfTarget, String player)} methods in config
 * records. Placeholders supported:
 *
 * <ul>
 *   <li>{@code {player}} — replaced by the target player's name
 *   <li>{@code {gamemode}} — replaced by the gamemode display name
 * </ul>
 */
public record MessagePair(String self, String other) {

  public MessagePair {
    Objects.requireNonNull(self, "self");
    Objects.requireNonNull(other, "other");
  }

  /** Returns {@code self} when {@code selfTarget} is true, otherwise {@code other}. */
  public String select(boolean selfTarget) {
    return selfTarget ? self : other;
  }

  /**
   * Formats the appropriate template replacing {@code {player}}.
   *
   * @param selfTarget whether the target is the actor themselves
   * @param player the target player's name
   */
  public String format(boolean selfTarget, String player) {
    return select(selfTarget).replace("{player}", player);
  }

  /**
   * Formats the appropriate template replacing {@code {player}} and {@code {gamemode}}.
   *
   * @param selfTarget whether the target is the actor themselves
   * @param player the target player's name
   * @param gamemode the gamemode display name
   */
  public String format(boolean selfTarget, String player, String gamemode) {
    return select(selfTarget).replace("{player}", player).replace("{gamemode}", gamemode);
  }
}
