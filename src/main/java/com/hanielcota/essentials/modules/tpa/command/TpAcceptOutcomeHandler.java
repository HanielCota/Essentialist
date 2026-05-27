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
import org.bukkit.entity.Player;

/**
 * Routes the two phases of a {@code /tpaccept}: the synchronous claim outcome (so accepter and
 * requester are notified immediately) and the deferred teleport outcome (so a teleport failure can
 * be reported without making the success messages wait for the async teleport to land).
 *
 * <p>{@link #handleAutoClaim} mirrors {@link #handleClaim} for the auto-accept-favorite path: the
 * target gets a distinct notice (they did not click anything), and the requester — not the target —
 * receives any failure feedback.
 */
@RequiredArgsConstructor
public final class TpAcceptOutcomeHandler {

  private final ConfigHandle<TpaConfig> config;
  private final TpaRequestReplyNotifier replyNotifier;
  private final TpaNotifier notifier;
  private final Map<AcceptOutcome, ResultRoute> routes = buildRoutes();

  private Map<AcceptOutcome, ResultRoute> buildRoutes() {
    var map = new EnumMap<AcceptOutcome, ResultRoute>(AcceptOutcome.class);
    map.put(AcceptOutcome.ACCEPTED, this::notifyAccepted);
    map.put(AcceptOutcome.REQUESTER_OFFLINE, this::notifyRequesterOffline);
    map.put(AcceptOutcome.TARGET_OFFLINE, this::notifyTargetOffline);
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

  /**
   * Auto-accept path. Sends an {@code autoAcceptedNotice} to the target on success (instead of
   * {@code acceptedSelf}), routes any failure to the {@code requesterActor} so the sender — not the
   * target — finds out, and still replies the accepted/denied line to the requester via {@link
   * TpaRequestReplyNotifier} on success.
   */
  public void handleAutoClaim(
      @NonNull AcceptOutcome result,
      @NonNull TeleportRequest request,
      @NonNull Player target,
      @NonNull CommandActor requesterActor) {
    var snap = this.config.value();
    var messages = snap.messages();

    switch (result) {
      case ACCEPTED -> autoAccepted(request, target, messages);
      case REQUESTER_OFFLINE -> {
        var requesterName = request.requester().name();
        var msg = messages.requesterOffline().replace("{player}", requesterName);
        requesterActor.sendError(msg);
      }
      case TARGET_OFFLINE -> {
        var targetName = request.target().name();
        var msg = messages.targetOffline().replace("{player}", targetName);
        requesterActor.sendError(msg);
      }
      case NOT_FOUND -> requesterActor.sendError(messages.noIncoming());
    }
  }

  private void autoAccepted(
      @NonNull TeleportRequest request, @NonNull Player target, @NonNull TpaMessages messages) {
    var requesterName = request.requester().name();
    this.notifier.notifyAutoAccepted(target, requesterName);

    var acceptedTemplate = messages.accepted();
    this.replyNotifier.notifyAccepted(request, acceptedTemplate);
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
}
