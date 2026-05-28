package com.hanielcota.essentials.modules.msg.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.msg.config.MsgConfig;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Renders and delivers the outgoing/incoming pair of a private message to sender and target. Owns
 * the template-fill logic — the config record stays a pure data carrier.
 */
@RequiredArgsConstructor
public final class MsgNotifier {

  private final @NonNull ConfigHandle<MsgConfig> config;
  private final @NonNull ActorFactory actors;

  public void notifyExchange(@NonNull Player sender, @NonNull Player target, @NonNull String body) {
    var snap = this.config.value();
    var senderName = sender.getName();
    var targetName = target.getName();

    var outgoingMsg = fill(snap.outgoingFormat(), senderName, targetName, body);
    var incomingMsg = fill(snap.incomingFormat(), senderName, targetName, body);

    var senderActor = this.actors.actorOf(sender);
    var targetActor = this.actors.actorOf(target);

    senderActor.sendMessage(outgoingMsg);
    targetActor.sendMessage(incomingMsg);
  }

  private static String fill(
      @NonNull String template,
      @NonNull String sender,
      @NonNull String target,
      @NonNull String body) {
    var withSender = template.replace("{sender}", sender);
    var withTarget = withSender.replace("{target}", target);
    return withTarget.replace("{message}", body);
  }
}
