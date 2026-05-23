package com.hanielcota.essentials.command;

import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

/**
 * Helpers around {@link CommandActor}.
 *
 * <p>{@code CommandActor.uniqueId()} returns a {@link String}, not a {@link java.util.UUID}, so the
 * obvious-looking {@code actor.uniqueId().equals(player.getUniqueId())} always returns {@code
 * false}. Comparing the {@code UUID.toString()} is correct but easy to forget — hence this helper.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Senders {

  /** Whether {@code actor} is the same identity as {@code subject}. */
  public static boolean isSelf(@NonNull CommandActor actor, @NonNull OfflinePlayer subject) {
    var actorUniqueId = actor.uniqueId();

    var subjectUuid = subject.getUniqueId();
    var subjectUniqueIdStr = subjectUuid.toString();

    return actorUniqueId.equals(subjectUniqueIdStr);
  }
}
