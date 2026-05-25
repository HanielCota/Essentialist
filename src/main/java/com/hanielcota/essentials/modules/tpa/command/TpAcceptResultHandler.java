package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaMessages;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.AcceptResult;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Routes the {@link AcceptResult} of {@code /tpaccept} to the accepter (own message) and, on
 * success, to the requester (out-of-band reply). Keeps the command class free of the multi-branch
 * switch and {@code .replace} chains.
 */
@RequiredArgsConstructor
public final class TpAcceptResultHandler {

  private final ConfigHandle<TpaConfig> config;
  private final PaperCommandFramework framework;
  private final PlayerProvider players;

  public void handle(
      @NonNull AcceptResult result, @NonNull TeleportRequest request, @NonNull CommandActor actor) {
    var snap = this.config.value();
    var messages = snap.messages();

    switch (result) {
      case SUCCESS -> handleSuccess(request, messages, actor);
      case REQUESTER_OFFLINE -> handleRequesterOffline(request, messages, actor);
      case TELEPORT_FAILED -> actor.sendError(messages.teleportFailed());
      case NOT_FOUND -> actor.sendError(messages.noIncoming());
    }
  }

  private void handleSuccess(
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    var requesterName = request.requester().name();
    var acceptedSelfTemplate = messages.acceptedSelf();
    var acceptedMsg = acceptedSelfTemplate.replace("{player}", requesterName);

    actor.sendSuccess(acceptedMsg);

    var acceptedTemplate = messages.accepted();
    TpaRequests.replyRequester(this.framework, this.players, request, acceptedTemplate, true);
  }

  private void handleRequesterOffline(
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    var requesterName = request.requester().name();
    var requesterOfflineTemplate = messages.requesterOffline();
    var offlineMsg = requesterOfflineTemplate.replace("{player}", requesterName);

    actor.sendError(offlineMsg);
  }
}
