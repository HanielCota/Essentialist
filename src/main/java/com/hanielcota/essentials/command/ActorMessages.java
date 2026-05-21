package com.hanielcota.essentials.command;

import com.hanielcota.essentials.config.MessagePair;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Objects;
import org.bukkit.entity.Player;

/**
 * Semantic wrapper around {@link PaperCommandFramework} for commands that need to send messages to
 * targeted players.
 *
 * <p>Eliminates the low-level {@code framework.actorOf(player)} and {@code Commands.isSelf()}
 * boilerplate by exposing only the messaging operations command handlers actually need.
 */
public final class ActorMessages {

  private final PaperCommandFramework framework;

  public ActorMessages(PaperCommandFramework framework) {
    this.framework = Objects.requireNonNull(framework, "framework");
  }

  /**
   * Sends {@code senderMsg} to the actor and {@code targetMsg} to the target player, suppressing
   * the target message when both refer to the same actor.
   */
  public void notifyTarget(CommandActor sender, Player target, String senderMsg, String targetMsg) {
    sender.sendDualMessage(framework.actorOf(target), senderMsg, targetMsg);
  }

  /**
   * Convenience overload that formats a {@link MessagePair} automatically, deciding between self
   * and other by comparing actor unique ids.
   *
   * <p>Eliminates the {@code boolean selfTarget = Commands.isSelf(...)} check entirely:
   *
   * <pre>
   * messages.notifyTarget(sender, subject, pair, subject.getName());
   * </pre>
   */
  public void notifyTarget(
      CommandActor sender, Player target, MessagePair pair, String playerName) {
    boolean selfTarget = Commands.isSelf(sender, target);
    String senderMsg = pair.format(selfTarget, playerName);
    String targetMsg = pair.self().replace("{player}", playerName);
    notifyTarget(sender, target, senderMsg, targetMsg);
  }

  /** Wraps a {@link Player} into a {@link CommandActor} with framework wiring. */
  public CommandActor actorOf(Player player) {
    return framework.actorOf(player);
  }
}
