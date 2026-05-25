package com.hanielcota.essentials.modules.speed.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.MessagePair;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Sender + target dual-message feedback for {@code /speed walk}, {@code fly} and {@code reset}.
 * Keeps the command class free of {@link CommandActor#sendDualMessage} plumbing.
 */
@RequiredArgsConstructor
public final class SpeedNotifier {

  private final ActorFactory actors;

  public void announce(
      @NonNull CommandActor sender, @NonNull Player subject, @NonNull MessagePair messages) {
    var name = subject.getName();
    var isSelf = Senders.isSelf(sender, subject);
    var target = this.actors.actorOf(subject);

    var senderMsg = messages.forSender(isSelf, name);
    var targetMsg = messages.forTarget(name);

    sender.sendDualMessage(target, senderMsg, targetMsg);
  }
}
