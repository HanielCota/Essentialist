package com.hanielcota.essentials.modules.msg.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.msg.config.MsgConfig;
import com.hanielcota.essentials.modules.socialspy.service.SocialSpyBroadcaster;
import com.hanielcota.essentials.service.ServiceRegistry;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Renders and delivers a private message to both parties and records them as last partners. Shared
 * by {@code /msg} and {@code /r} so the formatting + linking happens in one place.
 *
 * <p>After delivery, looks up {@link SocialSpyBroadcaster} via the service registry and forwards
 * the message to active spies if the broadcaster is available. The lookup is done on every send so
 * module load order between msg and socialspy does not matter.
 */
@RequiredArgsConstructor
public final class MsgDispatcher {

  private final ConfigHandle<MsgConfig> config;
  private final MsgService partners;
  private final PaperCommandFramework framework;
  private final ServiceRegistry registry;

  public void send(@NonNull Player sender, @NonNull Player target, @NonNull String body) {
    var snap = this.config.value();
    var senderName = sender.getName();
    var targetName = target.getName();
    var senderId = sender.getUniqueId();
    var targetId = target.getUniqueId();

    var outgoingMsg = snap.formatOutgoing(senderName, targetName, body);
    var incomingMsg = snap.formatIncoming(senderName, targetName, body);
    var senderActor = this.framework.actorOf(sender);
    var targetActor = this.framework.actorOf(target);

    this.partners.pair(senderId, targetId);

    senderActor.sendMessage(outgoingMsg);
    targetActor.sendMessage(incomingMsg);

    var spy = this.registry.find(SocialSpyBroadcaster.class).orElse(null);
    if (spy == null) {
      return;
    }

    spy.broadcast(senderId, senderName, targetId, targetName, body);
  }
}
