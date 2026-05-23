package com.hanielcota.essentials.config;

/**
 * Immutable pair of self / other message templates. Placeholders:
 *
 * <ul>
 *   <li>{@code {player}} — replaced by the target player's name.
 * </ul>
 *
 * <p>Other placeholders (e.g. {@code {gamemode}}) are expected to be pre-substituted by the config
 * record before producing the pair.
 */
public record MessagePair(String self, String other) {

  /**
   * Message from the sender's perspective.
   *
   * <p>Returns {@code self} when the sender is also the target, otherwise {@code other}. {@code
   * {player}} is replaced with {@code player}.
   */
  public String forSender(boolean selfTarget, String player) {
    return (selfTarget ? self : other).replace("{player}", player);
  }

  /**
   * Message from the target's perspective — always the {@code self} template with {@code {player}}
   * replaced.
   */
  public String forTarget(String player) {
    return self.replace("{player}", player);
  }
}
