package com.hanielcota.essentials.modules.speed.command;

import com.hanielcota.essentials.command.DualReply;
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
    DualReply.send(sender, subject, this.actors, messages);
  }
}
