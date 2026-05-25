package com.hanielcota.essentials.modules.msg.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.msg.config.MsgConfig;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Renders and delivers the outgoing/incoming pair of a private message to sender and target. Split
 * from {@link MsgDispatcher} so the dispatcher only orchestrates and the notifier owns the
 * formatting + dispatch.
 */
@RequiredArgsConstructor
public final class MsgNotifier {

  private final ConfigHandle<MsgConfig> config;
  private final ActorFactory actors;

  public void notifyExchange(@NonNull Player sender, @NonNull Player target, @NonNull String body) {
    var snap = this.config.value();
    var senderName = sender.getName();
    var targetName = target.getName();

    var outgoingMsg = snap.formatOutgoing(senderName, targetName, body);
    var incomingMsg = snap.formatIncoming(senderName, targetName, body);

    var senderActor = this.actors.actorOf(sender);
    var targetActor = this.actors.actorOf(target);

    senderActor.sendMessage(outgoingMsg);
    targetActor.sendMessage(incomingMsg);
  }
}
