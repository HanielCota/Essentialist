package com.hanielcota.essentials.modules.homes.menu.options;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.homes.menu.HomesActionTarget;
import com.hanielcota.essentials.modules.homes.menu.HomesMenu;
import com.hanielcota.essentials.modules.homes.menu.delete.DeleteHomeDialog;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameOrchestrator;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.service.HomeTeleporter;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Routes the buttons of {@link HomeOptionsMenu} (right-click on a home). Reads the active home name
 * from {@link HomesActionTarget} (set by {@link HomeClickHandler} when the player right-clicks).
 *
 * <p>Teleport and rename consume the target (the flow is over for the menu); change-icon and delete
 * leave it for the next sub-menu to pick up.
 */
@RequiredArgsConstructor
public final class HomeOptionsClickHandler {

  private final HomesActionTarget target;
  private final HomeService service;
  private final HomeTeleporter teleporter;
  private final HomeRenameOrchestrator rename;
  private final ActorFactory actors;

  void teleport(@NonNull ClickContext click) {
    var player = click.player();
    var uuid = player.getUniqueId();
    var homeName = this.target.consume(uuid);

    if (homeName == null) {
      click.close();
      return;
    }

    var home = this.service.findHome(uuid, homeName);
    if (home.isEmpty()) {
      click.close();
      return;
    }

    click.close();
    var actor = this.actors.actorOf(player);
    this.teleporter.teleport(player, home.get(), actor);
  }

  void rename(@NonNull ClickContext click) {
    var player = click.player();
    var uuid = player.getUniqueId();
    var homeName = this.target.consume(uuid);

    if (homeName == null) {
      click.close();
      return;
    }

    click.close();
    this.rename.prompt(player, homeName);
  }

  void changeIcon(@NonNull ClickContext click) {
    click.switchTo(MaterialCategoryMenu.ID);
  }

  void togglePin(@NonNull ClickContext click) {
    var player = click.player();
    var uuid = player.getUniqueId();
    var homeName = this.target.peek(uuid);

    if (homeName == null) {
      click.close();
      return;
    }

    var home = this.service.findHome(uuid, homeName);
    if (home.isEmpty()) {
      click.close();
      return;
    }

    this.service.setPinned(uuid, homeName, !home.get().pinned());
    click.refresh();
  }

  void delete(@NonNull ClickContext click) {
    click.switchTo(DeleteHomeDialog.ID);
  }

  void back(@NonNull ClickContext click) {
    var uuid = click.player().getUniqueId();
    this.target.clear(uuid);

    click.switchTo(HomesMenu.ID);
  }
}
