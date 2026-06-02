package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.service.HomeTeleporter;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Teleports the clicking player to a shared home as a visitor. Uses {@link
 * HomeTeleporter#teleportVisit} so the offline owner's usage is never recorded. Entry to the menu
 * is gated by the command's permission, so no extra check is needed here.
 */
@RequiredArgsConstructor
public final class PublicHomesClickHandler {

  private final HomeTeleporter teleporter;
  private final ActorFactory actors;

  void visit(@NonNull ClickContext click, @NonNull Home home) {
    var player = click.player();

    click.close();

    var actor = this.actors.actorOf(player);
    this.teleporter.teleportVisit(player, home, actor);
  }
}
