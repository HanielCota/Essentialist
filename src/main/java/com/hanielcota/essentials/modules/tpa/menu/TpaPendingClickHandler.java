package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptResultHandler;
import com.hanielcota.essentials.modules.tpa.command.TpaRequestReplyNotifier;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.AcceptResult;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.ClickType;

/**
 * Routes per-request clicks (accept / deny / block) on the pending menu. Bulk accept-all and
 * deny-all live in {@link TpaPendingBulkActions} so the menu can delegate them without dragging
 * those dependencies through this handler.
 */
@RequiredArgsConstructor
public final class TpaPendingClickHandler {

  private final ConfigHandle<TpaConfig> config;
  private final TeleportRequestService service;
  private final TpAcceptResultHandler acceptHandler;
  private final TpaRequestReplyNotifier replyNotifier;
  private final MainThreadCallbacks callbacks;
  private final ActorFactory actors;
  private final TpaBlockService blocks;
  private final TpaPendingBulkActions bulkActions;

  public void handle(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var clickType = click.clickType();
    if (clickType == ClickType.SHIFT_LEFT || clickType == ClickType.SHIFT_RIGHT) {
      block(click, request);
      return;
    }
    if (clickType.isRightClick()) {
      deny(click, request);
      return;
    }

    accept(click, request);
  }

  public void acceptAll(@NonNull ClickContext click) {
    this.bulkActions.acceptAll(click);
  }

  public void denyAll(@NonNull ClickContext click) {
    this.bulkActions.denyAll(click);
  }

  private void block(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var actor = this.actors.actorOf(click.player());
    var messages = this.config.value().messages();
    var requesterId = request.requester().id();
    var requesterName = request.requester().name();
    var viewerId = click.player().getUniqueId();

    this.service.deny(request);
    this.blocks.block(viewerId, requesterId, requesterName);

    var blockedMsg = messages.blockedPlayer().replace("{player}", requesterName);
    actor.sendSuccess(blockedMsg);

    click.refresh();
  }

  private void accept(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var actor = this.actors.actorOf(click.player());
    var claim = this.service.tryAccept(request);

    this.acceptHandler.handleClaim(claim, request, actor);

    if (claim != AcceptResult.ACCEPTED) {
      click.refresh();
      return;
    }

    click.close();
    var pending = this.service.dispatchTeleport(request);
    this.callbacks.hop(
        pending, success -> this.acceptHandler.handleTeleportOutcome(success, actor), "tpa menu");
  }

  private void deny(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var actor = this.actors.actorOf(click.player());
    var messages = this.config.value().messages();

    this.service.deny(request);

    var requesterName = request.requester().name();
    var deniedSelf = messages.deniedSelf().replace("{player}", requesterName);
    actor.sendSuccess(deniedSelf);

    var deniedTemplate = messages.denied();
    this.replyNotifier.notifyDenied(request, deniedTemplate);
    click.refresh();
  }
}
