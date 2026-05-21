package com.hanielcota.essentials.command;

import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import org.bukkit.entity.Player;

/** Low-boilerplate helpers for Essentials commands. */
public final class Commands {

  private Commands() {}

  /** Whether the actor represents the given player. */
  public static boolean isSelf(CommandActor actor, Player player) {
    return actor.uniqueId() != null && actor.uniqueId().equals(player.getUniqueId());
  }

  /**
   * Sends {@code selfMessage} to the actor and {@code otherMessage} to the target, unless they are
   * the same player.
   */
  public static void notifyTarget(
      CommandActor actor,
      PaperCommandFramework framework,
      Player target,
      String selfMessage,
      String otherMessage) {
    actor.sendDualMessage(framework.actorOf(target), selfMessage, otherMessage);
  }
}
