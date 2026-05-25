package com.hanielcota.essentials.modules.title.service;

import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.UUID;
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
 * delivered to an online player whose name happens to be {@code Hello}.
 *
 * <p>The target is identified by {@link UUID} + name snapshot rather than a live {@link Player}
 * reference so the record stays valid past the parse tick and the command path stays safe even if
 * the player disconnects between parse and dispatch.
 */
public record TitleRequest(@Nullable UUID targetId, @Nullable String targetName, String message) {

  public static TitleRequest from(
      @Nullable Player self, @NonNull String input, @NonNull PlayerProvider players) {
    var trimmedInput = input.strip();

    if (trimmedInput.startsWith("\"")) {
      return forSelf(self, trimmedInput);
    }

    var space = trimmedInput.indexOf(' ');
    if (space <= 0) {
      return forSelf(self, trimmedInput);
    }

    var candidateName = trimmedInput.substring(0, space);
    var tail = trimmedInput.substring(space + 1);
    var rest = tail.strip();

    if (!rest.startsWith("\"")) {
      return forSelf(self, trimmedInput);
    }

    var namedTarget = players.online(candidateName).orElse(null);
    if (namedTarget == null) {
      return forSelf(self, trimmedInput);
    }

    return new TitleRequest(namedTarget.getUniqueId(), namedTarget.getName(), rest);
  }

  private static TitleRequest forSelf(@Nullable Player self, @NonNull String message) {
    if (self == null) {
      return new TitleRequest(null, null, message);
    }
    return new TitleRequest(self.getUniqueId(), self.getName(), message);
  }
}
