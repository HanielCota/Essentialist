package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptResultHandler;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.AcceptResult;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import lombok.NonNull;

public final class TpaPendingClickHandler {

  private final ConfigHandle<TpaConfig> config;
  private final TeleportRequestService service;
  private final TpAcceptResultHandler acceptHandler;
  private final MainThreadCallbacks callbacks;
  private final ActorFactory actors;
  private final PlayerProvider players;

  public TpaPendingClickHandler(
      @NonNull ConfigHandle<TpaConfig> config,
      @NonNull TeleportRequestService service,
      @NonNull TpAcceptResultHandler acceptHandler,
      @NonNull MainThreadCallbacks callbacks,
      @NonNull ActorFactory actors,
      @NonNull PlayerProvider players) {
    this.config = config;
    this.service = service;
    this.acceptHandler = acceptHandler;
    this.callbacks = callbacks;
    this.actors = actors;
    this.players = players;
  }

  public void handle(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    if (click.clickType().isRightClick()) {
      deny(click, request);
      return;
    }

    accept(click, request);
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

    replyRequester(request);
    click.refresh();
  }

  private void replyRequester(@NonNull TeleportRequest request) {
    var requesterId = request.requester().id();
    var requester = this.players.online(requesterId).orElse(null);
    if (requester == null) {
      return;
    }

    var actor = this.actors.actorOf(requester);
    var targetName = request.target().name();
    var denied = this.config.value().messages().denied().replace("{player}", targetName);

    actor.sendError(denied);
  }
}
