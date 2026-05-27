package com.hanielcota.essentials.modules.tpa.menu.pending;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptOutcomeHandler;
import com.hanielcota.essentials.modules.tpa.command.TpaRequestReplyNotifier;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaMessages;
import com.hanielcota.essentials.modules.tpa.domain.AcceptOutcome;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Handles the accept-all / deny-all shortcuts of the pending menu. Split from {@link
 * TpaPendingClickHandler} so the per-request and bulk paths each own their concerns.
 */
@RequiredArgsConstructor
public final class TpaPendingBulkActions {

  private final ConfigHandle<TpaConfig> config;
  private final TeleportRequestService service;
  private final TpAcceptOutcomeHandler acceptHandler;
  private final TpaRequestReplyNotifier replyNotifier;
  private final MainThreadCallbacks callbacks;
  private final ActorFactory actors;

  public void acceptAll(@NonNull ClickContext click) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var snap = this.config.value();
    var messages = snap.messages();

    var pending = this.service.incoming(viewerId);
    if (pending.isEmpty()) {
      actor.sendError(messages.noIncoming());
      click.refresh();
      return;
    }

    var toAccept = requestsToAccept(pending);
    var skipped = pending.size() - toAccept.size();
    var accepted = claimAndDispatch(toAccept, actor, messages);

    var countText = Integer.toString(accepted);
    var summary = messages.acceptedAllMessage().replace("{count}", countText);
    actor.sendSuccess(summary);

    if (skipped > 0) {
      var skippedText = Integer.toString(skipped);
      var skippedMsg = messages.tpaHerePriorityMessage().replace("{count}", skippedText);
      actor.sendError(skippedMsg);
    }

    click.refresh();
  }

  public void denyAll(@NonNull ClickContext click) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var snap = this.config.value();
    var messages = snap.messages();

    var pending = this.service.incoming(viewerId);
    if (pending.isEmpty()) {
      actor.sendError(messages.noIncoming());
      click.refresh();
      return;
    }

    var deniedCount = 0;
    var alreadyProcessed = 0;
    for (var request : pending) {
      var denied = this.service.deny(request);
      if (denied) {
        this.replyNotifier.notifyDenied(request, messages.denied());
        deniedCount++;
      } else {
        alreadyProcessed++;
      }
    }

    var countText = Integer.toString(deniedCount);
    var summary = messages.deniedAllMessage().replace("{count}", countText);
    actor.sendSuccess(summary);

    if (alreadyProcessed > 0) {
      var processedText = Integer.toString(alreadyProcessed);
      var processedMsg = messages.alreadyProcessedMessage().replace("{count}", processedText);
      actor.sendError(processedMsg);
    }

    click.refresh();
  }

  private int claimAndDispatch(
      @NonNull List<TeleportRequest> pending,
      @NonNull CommandActor actor,
      @NonNull TpaMessages messages) {
    var accepted = 0;
    var alreadyProcessed = 0;
    for (var request : pending) {
      var claim = this.service.tryAccept(request);
      if (claim == AcceptOutcome.NOT_FOUND) {
        alreadyProcessed++;
        continue;
      }
      if (claim != AcceptOutcome.ACCEPTED) {
        continue;
      }
      this.replyNotifier.notifyAccepted(request, messages.accepted());
      var pendingTeleport = this.service.dispatchTeleport(request);
      this.callbacks.hop(
          pendingTeleport,
          success -> this.acceptHandler.handleTeleportOutcome(success, actor),
          "tpa accept-all");
      accepted++;
    }
    if (alreadyProcessed > 0) {
      var processedText = Integer.toString(alreadyProcessed);
      var processedMsg = messages.alreadyProcessedMessage().replace("{count}", processedText);
      actor.sendError(processedMsg);
    }
    return accepted;
  }

  private static List<TeleportRequest> requestsToAccept(@NonNull List<TeleportRequest> pending) {
    for (var request : pending) {
      if (request.type() == TeleportRequestType.TPAHERE) {
        return List.of(request);
      }
    }
    return pending;
  }
}
