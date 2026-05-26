package com.hanielcota.essentials.modules.msg.command;

import com.hanielcota.essentials.modules.msg.service.MsgService;
import com.hanielcota.essentials.modules.msg.service.SocialSpyBridge;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Orchestrates a single private-message exchange: records the pairing for {@code /r}, dispatches
 * the dual outgoing/incoming messages via {@link MsgNotifier} and forwards to active spies via
 * {@link SocialSpyBridge}. Owns no formatting and no cross-module registry lookup — both are
 * delegated.
 */
@RequiredArgsConstructor
public final class MsgDispatcher {

  private final MsgService partners;
  private final MsgNotifier notifier;
  private final SocialSpyBridge spyBridge;

  public void send(@NonNull Player sender, @NonNull Player target, @NonNull String body) {
    var senderId = sender.getUniqueId();
    var targetId = target.getUniqueId();
    var senderName = sender.getName();
    var targetName = target.getName();

    this.partners.pair(senderId, targetId);
    this.notifier.notifyExchange(sender, target, body);
    this.spyBridge.notifySpies(senderId, senderName, targetId, targetName, body);
  }
}
