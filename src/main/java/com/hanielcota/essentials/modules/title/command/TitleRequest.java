package com.hanielcota.essentials.modules.title.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * The recipient and message parsed from {@code /title} input.
 *
 * <p>The first token is the target only when it names an online player; otherwise the whole input
 * is the message and the target is the sender ({@code null} when the sender is the console).
 */
public record TitleRequest(Player target, String message) {

  public static TitleRequest from(Player self, String input) {
    int space = input.indexOf(' ');
    if (space > 0) {
      Player named = Bukkit.getPlayerExact(input.substring(0, space));
      if (named != null) {
        return new TitleRequest(named, input.substring(space + 1).strip());
      }
    }
    return new TitleRequest(self, input);
  }
}
