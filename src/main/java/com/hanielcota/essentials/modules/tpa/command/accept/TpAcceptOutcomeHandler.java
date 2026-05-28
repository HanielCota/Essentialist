package com.hanielcota.essentials.modules.tpa.command.accept;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.send.TpaRequestReplyNotifier;
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
 * Routes the synchronous claim outcome of {@code /tpaccept}: each {@link AcceptOutcome} variant is
 * dispatched onto a notification method via the {@link #routes} table.
 *
 * <p>The deferred teleport outcome (success/failure messaging after the async teleport lands) lives
 * in {@link TpAcceptTeleportNotifier} — keeping the two flows in separate classes preserves SRP.
 */
@RequiredArgsConstructor
public final class TpAcceptOutcomeHandler {

  private final @NonNull ConfigHandle<TpaConfig> config;
  private final @NonNull TpaRequestReplyNotifier replyNotifier;
  private final Map<AcceptOutcome, ResultRoute> routes = buildRoutes();

  private Map<AcceptOutcome, ResultRoute> buildRoutes() {
    var map = new EnumMap<AcceptOutcome, ResultRoute>(AcceptOutcome.class);
    map.put(AcceptOutcome.ACCEPTED, this::notifyAccepted);
    map.put(AcceptOutcome.REQUESTER_OFFLINE, this::notifyRequesterOffline);
    map.put(AcceptOutcome.TARGET_OFFLINE, this::notifyTargetOffline);
    map.put(AcceptOutcome.NOT_FOUND, this::notifyNotFound);
    map.put(AcceptOutcome.CROSS_WORLD_REFUSED, this::notifyCrossWorldRefused);
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

  private void notifyTargetOffline(
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    var targetName = request.target().name();
    var targetOfflineTemplate = messages.targetOffline();
    var offlineMsg = targetOfflineTemplate.replace("{player}", targetName);

    actor.sendError(offlineMsg);
  }

  private void notifyNotFound(
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    actor.sendError(messages.noIncoming());
  }

  private void notifyCrossWorldRefused(
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    var requesterName = request.requester().name();
    var template = messages.crossWorldRefused();
    var msg = template.replace("{player}", requesterName);
    actor.sendError(msg);
  }
}
