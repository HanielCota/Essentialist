package com.hanielcota.essentials.modules.tpa.menu.pending;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptOutcomeHandler;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptTeleportNotifier;
import com.hanielcota.essentials.modules.tpa.command.TpaRequestReplyNotifier;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.AcceptOutcome;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.TpaPendingSelections;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TpaPendingActionClickHandler {

  private final @NonNull ConfigHandle<TpaConfig> config;
  private final @NonNull TeleportRequestService service;
  private final @NonNull TpaBlockService blocks;
  private final @NonNull TpaPendingSelections selections;
  private final @NonNull TpAcceptOutcomeHandler acceptHandler;
  private final @NonNull TpAcceptTeleportNotifier teleportNotifier;
  private final @NonNull TpaRequestReplyNotifier replyNotifier;
  private final @NonNull MainThreadCallbacks callbacks;
  private final @NonNull ActorFactory actors;

  void accept(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var claim = this.service.tryAccept(request);

    this.acceptHandler.handleClaim(claim, request, actor);
    this.selections.clear(viewerId);

    if (claim != AcceptOutcome.ACCEPTED) {
      click.switchTo(TpaPendingMenu.ID);
      return;
    }

    click.close();
    var pending = this.service.dispatchTeleport(request);
    this.callbacks.hop(
        pending,
        success -> this.teleportNotifier.notifyOutcome(success, actor),
        "tpa pending action accept");
  }

  void deny(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var messages = this.config.value().messages();

    var denied = this.service.deny(request);
    if (!denied) {
      actor.sendError(messages.noIncoming());
      this.selections.clear(viewerId);
      click.switchTo(TpaPendingMenu.ID);
      return;
    }

    var requesterName = request.requester().name();
    var deniedSelf = messages.deniedSelf().replace("{player}", requesterName);
    actor.sendSuccess(deniedSelf);

    var deniedTemplate = messages.denied();
    this.replyNotifier.notifyDenied(request, deniedTemplate);

    this.selections.clear(viewerId);
    click.switchTo(TpaPendingMenu.ID);
  }

  void block(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var messages = this.config.value().messages();
    var requesterId = request.requester().id();
    var requesterName = request.requester().name();

    var denied = this.service.deny(request);
    if (!denied) {
      actor.sendError(messages.noIncoming());
      this.selections.clear(viewerId);
      click.switchTo(TpaPendingMenu.ID);
      return;
    }

    this.blocks.block(viewerId, requesterId, requesterName);

    var blockedMsg = messages.blockedPlayer().replace("{player}", requesterName);
    actor.sendSuccess(blockedMsg);

    this.selections.clear(viewerId);
    click.switchTo(TpaPendingMenu.ID);
  }
}
