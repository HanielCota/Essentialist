package com.hanielcota.essentials.modules.tpa.menu.target;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.favorites.TpaFavoriteAddNotifier;
import com.hanielcota.essentials.modules.tpa.command.send.TpaSendOrchestrator;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaTargetSelection;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.selection.TpaTargetSelections;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TpaTargetActionClickHandler {

  private final @NonNull ConfigHandle<TpaConfig> config;
  private final @NonNull TpaTargetSelections selections;
  private final @NonNull TpaFavoriteService favorites;
  private final @NonNull TpaFavoriteAddNotifier addNotifier;
  private final @NonNull PlayerProvider players;
  private final @NonNull ActorFactory actors;
  private final @NonNull TpaSendOrchestrator dispatcher;

  void sendRequest(
      @NonNull ClickContext click,
      @NonNull TpaTargetSelection entry,
      @NonNull TeleportRequestType type) {
    var viewer = click.player();
    var actor = this.actors.actorOf(viewer);
    var snap = this.config.value();
    var messages = snap.messages();

    var resolved = this.players.online(entry.targetId());
    if (resolved.isEmpty()) {
      var offlineText = messages.favoriteOffline().replace("{player}", entry.targetName());
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

  void toggleFavorite(
      @NonNull ClickContext click, @NonNull TpaTargetSelection entry, boolean isFavorite) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var actor = this.actors.actorOf(viewer);
    var messages = this.config.value().messages();

    if (isFavorite) {
      this.favorites.remove(viewerId, entry.targetId());
      var removedText = messages.favoriteRemoved().replace("{player}", entry.targetName());
      actor.sendSuccess(removedText);
      click.refresh();
      return;
    }

    var added = this.favorites.add(viewerId, entry.targetId(), entry.targetName());
    if (!added) {
      var alreadyText = messages.favoriteAlready().replace("{player}", entry.targetName());
      actor.sendError(alreadyText);
      return;
    }

    var addedText = messages.favoriteAdded().replace("{player}", entry.targetName());
    actor.sendSuccess(addedText);
    this.addNotifier.notify(viewer.getName(), entry.targetId());
    click.refresh();
  }

  void back(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.selections.clear(viewerId);

    click.switchTo(TpaHelpMenu.ID);
  }
}
