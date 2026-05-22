package com.hanielcota.essentials.command;

import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.OfflinePlayer;

/**
 * Helpers around {@link CommandActor}.
 *
 * <p>{@code CommandActor.uniqueId()} returns a {@link String}, not a {@link java.util.UUID}, so the
 * obvious-looking {@code actor.uniqueId().equals(player.getUniqueId())} always returns {@code
 * false}. Comparing the {@code UUID.toString()} is correct but easy to forget — hence this helper.
 */
public final class Senders {

  private Senders() {}

  /** Whether {@code actor} is the same identity as {@code subject}. */
  public static boolean isSelf(CommandActor actor, OfflinePlayer subject) {
    return actor.uniqueId().equals(subject.getUniqueId().toString());
  }
}
