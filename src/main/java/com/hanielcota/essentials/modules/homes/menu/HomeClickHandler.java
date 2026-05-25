package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameOrchestrator;
import com.hanielcota.essentials.modules.homes.teleport.HomeTeleporter;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.ClickType;

/**
 * Routes a /homes menu click to one of four sub-flows by {@link ClickType}: LEFT teleports via
 * {@link HomeTeleporter}, RIGHT opens the delete dialog, SHIFT+click starts the chat rename, DROP
 * (Q) opens the material picker. The home-name sub-flows pre-populate {@link HomesActionTarget} so
 * the opened menu/dialog knows which home to act on.
 */
@RequiredArgsConstructor
public final class HomeClickHandler {

  private final HomeTeleporter teleporter;
  private final ActorFactory actors;
  private final HomesActionTarget target;
  private final HomeRenameOrchestrator rename;

  public void handle(@NonNull ClickContext click, @NonNull Home home) {
    var type = click.clickType();
    var player = click.player();
    var homeName = home.name();

    if (type.isShiftClick()) {
      click.close();
      this.rename.prompt(player, homeName);
      return;
    }

    var isDrop = type == ClickType.DROP || type == ClickType.CONTROL_DROP;
    if (isDrop) {
      openSubMenuFor(click, homeName, MaterialCategoryMenu.ID);
      return;
    }

    if (type.isRightClick()) {
      openSubMenuFor(click, homeName, DeleteHomeDialog.ID);
      return;
    }

    click.close();

    var actor = this.actors.actorOf(player);
    this.teleporter.teleport(player, home, actor);
  }

  private void openSubMenuFor(
      @NonNull ClickContext click, @NonNull String homeName, @NonNull String menuId) {
    var player = click.player();
    var uuid = player.getUniqueId();

    this.target.set(uuid, homeName);
    click.switchTo(menuId);
  }
}
