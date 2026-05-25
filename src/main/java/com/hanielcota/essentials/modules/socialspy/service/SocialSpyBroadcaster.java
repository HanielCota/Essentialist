package com.hanielcota.essentials.modules.socialspy.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.socialspy.config.SocialSpyConfig;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Sends a spy line to every active spy when a private message is dispatched. Skips the message's
 * sender and target — they already see the message on their own. Skips offline spy ids defensively
 * even though {@link com.hanielcota.essentials.modules.socialspy.listener.SocialSpyQuitListener}
 * drops them on quit.
 */
@RequiredArgsConstructor
public final class SocialSpyBroadcaster {

  private final ConfigHandle<SocialSpyConfig> config;
  private final SocialSpyService service;
  private final PlayerProvider players;
  private final PaperCommandFramework framework;

  public void broadcast(
      @NonNull UUID senderId,
      @NonNull String senderName,
      @NonNull UUID targetId,
      @NonNull String targetName,
      @NonNull String body) {
    var active = this.service.spies();
    if (active.isEmpty()) {
      return;
    }

    var snap = this.config.value();
    var line = snap.formatSpy(senderName, targetName, body);

    for (var spyId : active) {
      if (spyId.equals(senderId) || spyId.equals(targetId)) {
        continue;
      }
      var spy = this.players.online(spyId).orElse(null);
      if (spy == null) {
        continue;
      }
      var actor = this.framework.actorOf(spy);
      actor.sendMessage(line);
    }
  }
}
