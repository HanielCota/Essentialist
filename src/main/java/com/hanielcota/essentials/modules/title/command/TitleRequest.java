package com.hanielcota.essentials.modules.title.command;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * The recipient and message parsed from {@code /title} input.
 *
 * <p>When the input starts with a quote, the whole input is the message and there is no named
 * target. Otherwise the first token is the target when it names an online player; if it does not,
 * the whole input is the message. The target is the sender ({@code null} for the console) whenever
 * none is named.
 */
public record TitleRequest(Player target, String message) {

  public static TitleRequest from(Player self, String input) {
    Objects.requireNonNull(input, "input");

    var trimmedInput = input.strip();

    if (trimmedInput.startsWith("\"")) {
      return new TitleRequest(self, trimmedInput);
    }

    var space = trimmedInput.indexOf(' ');
    if (space > 0) {
      var candidateName = trimmedInput.substring(0, space);
      var namedTarget = Bukkit.getPlayerExact(candidateName);

      if (namedTarget != null) {
        var cleanMessage = trimmedInput.substring(space + 1).strip();
        return new TitleRequest(namedTarget, cleanMessage);
      }
    }

    return new TitleRequest(self, trimmedInput);
  }
}
