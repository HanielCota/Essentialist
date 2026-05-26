package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaMessages;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.AcceptResult;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Routes the two phases of a {@code /tpaccept}: the synchronous claim outcome (so accepter and
 * requester are notified immediately) and the deferred teleport outcome (so a teleport failure can
 * be reported without making the success messages wait for the async teleport to land).
 */
@RequiredArgsConstructor
public final class TpAcceptResultHandler {

  private final ConfigHandle<TpaConfig> config;
  private final TpaRequestReplyNotifier replyNotifier;

  public void handleClaim(
      @NonNull AcceptResult result, @NonNull TeleportRequest request, @NonNull CommandActor actor) {
    var snap = this.config.value();
    var messages = snap.messages();

    switch (result) {
      case ACCEPTED -> notifyAccepted(request, messages, actor);
      case REQUESTER_OFFLINE -> notifyRequesterOffline(request, messages, actor);
      case NOT_FOUND -> actor.sendError(messages.noIncoming());
    }
  }

  public void handleTeleportOutcome(boolean success, @NonNull CommandActor actor) {
    if (success) {
      return;
    }

    var snap = this.config.value();
    var messages = snap.messages();

    actor.sendError(messages.teleportFailed());
  }

  private void notifyAccepted(
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    var requesterName = request.requester().name();
    var acceptedSelfTemplate = messages.acceptedSelf();
    var acceptedMsg = acceptedSelfTemplate.replace("{player}", requesterName);

    actor.sendSuccess(acceptedMsg);

    var acceptedTemplate = messages.accepted();
    this.replyNotifier.notifyAccepted(request, acceptedTemplate);
  }

  private void notifyRequesterOffline(
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    var requesterName = request.requester().name();
    var requesterOfflineTemplate = messages.requesterOffline();
    var offlineMsg = requesterOfflineTemplate.replace("{player}", requesterName);

    actor.sendError(offlineMsg);
  }
}
