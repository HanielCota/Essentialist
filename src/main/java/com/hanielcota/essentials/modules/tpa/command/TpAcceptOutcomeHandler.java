package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaMessages;
import com.hanielcota.essentials.modules.tpa.domain.AcceptOutcome;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.EnumMap;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Routes the two phases of a {@code /tpaccept}: the synchronous claim outcome (so accepter and
 * requester are notified immediately) and the deferred teleport outcome (so a teleport failure can
 * be reported without making the success messages wait for the async teleport to land).
 */
@RequiredArgsConstructor
public final class TpAcceptOutcomeHandler {

  private final ConfigHandle<TpaConfig> config;
  private final TpaRequestReplyNotifier replyNotifier;
  private final Map<AcceptOutcome, ResultRoute> routes = buildRoutes();

  private Map<AcceptOutcome, ResultRoute> buildRoutes() {
    var map = new EnumMap<AcceptOutcome, ResultRoute>(AcceptOutcome.class);
    map.put(AcceptOutcome.ACCEPTED, this::notifyAccepted);
    map.put(AcceptOutcome.REQUESTER_OFFLINE, this::notifyRequesterOffline);
    map.put(AcceptOutcome.NOT_FOUND, this::notifyNotFound);
    return Map.copyOf(map);
  }

  @FunctionalInterface
  private interface ResultRoute {
    void handle(
        @NonNull TeleportRequest request,
        @NonNull TpaMessages messages,
        @NonNull CommandActor actor);
  }

  public void handleClaim(
      @NonNull AcceptOutcome result,
      @NonNull TeleportRequest request,
      @NonNull CommandActor actor) {
    var snap = this.config.value();
    var messages = snap.messages();

    var route = this.routes.get(result);
    if (route != null) {
      route.handle(request, messages, actor);
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

  private void notifyNotFound(
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    actor.sendError(messages.noIncoming());
  }
}
