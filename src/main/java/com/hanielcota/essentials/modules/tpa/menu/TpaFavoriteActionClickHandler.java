package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.TpaSendOrchestrator;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TpaFavoriteActionClickHandler {

  private final @NonNull ConfigHandle<TpaConfig> config;
  private final @NonNull TpaFavoriteService favorites;
  private final @NonNull TpaFavoriteSelections selections;
  private final @NonNull TeleportRequestService requests;
  private final @NonNull PlayerProvider players;
  private final @NonNull ActorFactory actors;
  private final @NonNull TpaSendOrchestrator dispatcher;

  void sendRequest(
      @NonNull ClickContext click, @NonNull TpaFavorite entry, @NonNull TeleportRequestType type) {
    var viewer = click.player();
    var actor = this.actors.actorOf(viewer);
    var snap = this.config.value();
    var messages = snap.messages();

    var resolved = this.players.online(entry.favoriteId());
    if (resolved.isEmpty()) {
      var offlineText = messages.favoriteOffline().replace("{player}", entry.favoriteName());
      actor.sendError(offlineText);
      return;
    }

    var target = resolved.get();
    var confirmationTemplate =
        type == TeleportRequestType.TPA ? messages.requestSent() : messages.requestSentHere();

    click.close();
    this.selections.clear(viewer.getUniqueId());
    this.dispatcher.send(actor, target, type, confirmationTemplate);
  }

  void removeFavorite(@NonNull ClickContext click, @NonNull TpaFavorite entry) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);

    this.favorites.remove(viewerId, entry.favoriteId());
    this.selections.clear(viewerId);

    var messages = this.config.value().messages();
    var removedText = messages.favoriteRemoved().replace("{player}", entry.favoriteName());
    actor.sendSuccess(removedText);

    click.switchTo(TpaFavoritesMenu.ID);
  }

  void back(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.selections.clear(viewerId);

    click.switchTo(TpaFavoritesMenu.ID);
  }
}
