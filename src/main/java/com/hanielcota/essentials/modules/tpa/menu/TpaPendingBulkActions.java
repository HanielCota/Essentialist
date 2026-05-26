package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptResultHandler;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.AcceptResult;
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
  private final TpAcceptResultHandler acceptHandler;
  private final MainThreadCallbacks callbacks;
  private final ActorFactory actors;

  public void acceptAll(@NonNull ClickContext click) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var messages = this.config.value().messages();

    var pending = this.service.incoming(viewerId);
    if (pending.isEmpty()) {
      actor.sendError(messages.noIncoming());
      click.refresh();
      return;
    }

    var accepted = claimAndDispatch(pending, actor);

    var countText = Integer.toString(accepted);
    var summary = messages.acceptedAllMessage().replace("{count}", countText);
    actor.sendSuccess(summary);

    click.refresh();
  }

  public void denyAll(@NonNull ClickContext click) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var messages = this.config.value().messages();

    var pending = this.service.incoming(viewerId);
    if (pending.isEmpty()) {
      actor.sendError(messages.noIncoming());
      click.refresh();
      return;
    }

    for (var request : pending) {
      this.service.deny(request);
    }

    var countText = Integer.toString(pending.size());
    var summary = messages.deniedAllMessage().replace("{count}", countText);
    actor.sendSuccess(summary);

    click.refresh();
  }

  private int claimAndDispatch(
      @NonNull List<TeleportRequest> pending, @NonNull CommandActor actor) {
    var accepted = 0;
    for (var request : pending) {
      var claim = this.service.tryAccept(request);
      if (claim != AcceptResult.ACCEPTED) {
        continue;
      }
      var pendingTeleport = this.service.dispatchTeleport(request);
      this.callbacks.hop(
          pendingTeleport,
          success -> this.acceptHandler.handleTeleportOutcome(success, actor),
          "tpa accept-all");
      accepted++;
    }
    return accepted;
  }
}
