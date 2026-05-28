package com.hanielcota.essentials.modules.title.service;

import com.hanielcota.essentials.modules.title.domain.TitleRequest;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Parses {@code /title} input into a {@link TitleRequest}. Lives outside {@code domain/} so the
 * record stays a pure value carrier and the parsing rules (quote handling, named-target detection,
 * online lookup) live next to the rest of the module's services.
 */
@RequiredArgsConstructor
public final class TitleRequestParser {

  private final @NonNull PlayerProvider players;

  public TitleRequest parse(@Nullable Player self, @NonNull String input) {
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

    var namedTarget = this.players.online(candidateName).orElse(null);
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
