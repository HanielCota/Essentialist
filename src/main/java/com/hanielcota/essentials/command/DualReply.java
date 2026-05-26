package com.hanielcota.essentials.command;

import com.hanielcota.essentials.config.MessagePair;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Sender + target dual-message dispatch for the heal/feed/kill/repair-style commands that map a
 * single action onto two messages (one for the actor, a separate one for the affected player).
 *
 * <p>Resolves the self-target flag through {@link Senders}, formats both messages from {@link
 * MessagePair}, wraps the subject as a {@link CommandActor} via {@link ActorFactory} and emits in
 * one call. Replaces the four-line copy that was duplicated across every action command.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DualReply {

  /**
   * Sends {@code messages} as a sender / target pair, suppressing the duplicate when the sender
   * targets themselves.
   */
  public static void send(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      @NonNull ActorFactory actors,
      @NonNull MessagePair messages) {
    send(sender, subject, actors, messages, UnaryOperator.identity());
  }

  public static void send(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      @NonNull ActorFactory actors,
      @NonNull MessagePair messages,
      @NonNull UnaryOperator<String> formatter) {
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);
    var target = actors.actorOf(subject);

    var senderMsg = formatter.apply(messages.forSender(self, name));
    var targetMsg = formatter.apply(messages.forTarget(name));

    sender.sendDualMessage(target, senderMsg, targetMsg);
  }
}
