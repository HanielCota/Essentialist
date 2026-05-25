package com.hanielcota.essentials.modules.title.service;

import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * The recipient and message parsed from {@code /title} input.
 *
 * <p>When the input starts with a quote, the whole input is the message and there is no named
 * target. Otherwise the first token is treated as a named target only when the remainder is a
 * quoted title — matching {@code @Syntax("/title [jogador] "título"")}. Plain unquoted text like
 * {@code /title Hello there} stays as the message for {@code self} instead of silently being
 * delivered to an online player whose name happens to be {@code Hello}. The target is {@code self}
 * (which may be {@code null} for the console) whenever none is named.
 */
public record TitleRequest(@Nullable Player target, String message) {

  public static TitleRequest from(
      @Nullable Player self, @NonNull String input, @NonNull PlayerProvider players) {
    var trimmedInput = input.strip();

    if (trimmedInput.startsWith("\"")) {
      return new TitleRequest(self, trimmedInput);
    }

    var space = trimmedInput.indexOf(' ');
    if (space <= 0) {
      return new TitleRequest(self, trimmedInput);
    }

    var candidateName = trimmedInput.substring(0, space);
    var tail = trimmedInput.substring(space + 1);
    var rest = tail.strip();

    if (!rest.startsWith("\"")) {
      return new TitleRequest(self, trimmedInput);
    }

    var namedTarget = players.online(candidateName).orElse(null);
    if (namedTarget == null) {
      return new TitleRequest(self, trimmedInput);
    }

    return new TitleRequest(namedTarget, rest);
  }
}
