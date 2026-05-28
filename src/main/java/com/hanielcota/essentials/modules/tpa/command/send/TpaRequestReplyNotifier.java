package com.hanielcota.essentials.modules.tpa.command.send;

import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Sends the requester their accept/deny reply when they are still online. No-ops when the requester
 * has logged out so a late callback doesn't crash.
 */
@RequiredArgsConstructor
public final class TpaRequestReplyNotifier {

  private final ActorFactory actors;
  private final PlayerProvider players;

  public void notifyAccepted(@NonNull TeleportRequest request, @NonNull String template) {
    reply(request, template, true);
  }

  public void notifyDenied(@NonNull TeleportRequest request, @NonNull String template) {
    reply(request, template, false);
  }

  private void reply(
      @NonNull TeleportRequest request, @NonNull String template, boolean asSuccess) {
    var requesterId = request.requester().id();
    var requesterPlayer = this.players.online(requesterId).orElse(null);
    if (requesterPlayer == null) {
      return;
    }

    var requesterActor = this.actors.actorOf(requesterPlayer);

    var replyTargetName = request.target().name();
    var replyMsg = template.replace("{player}", replyTargetName);

    if (!asSuccess) {
      requesterActor.sendError(replyMsg);
      return;
    }
    requesterActor.sendSuccess(replyMsg);
  }
}
