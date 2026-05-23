package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ItemClickHandler;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.rename.HomeRenamePrompter;
import com.hanielcota.essentials.modules.homes.teleport.HomeTeleporter;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.time.Duration;
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
public final class HomeClickHandler implements ItemClickHandler<Home> {

  private static final Duration SUBMENU_OPEN_DELAY = Duration.ofMillis(150);

  private final HomeTeleporter teleporter;
  private final PaperCommandFramework framework;
  private final HomesActionTarget target;
  private final HomeRenamePrompter rename;
  private final Scheduler scheduler;
  private final MenuService menus;

  @Override
  public void handle(@NonNull ClickContext click, @NonNull Home home) {
    var type = click.clickType();
    var player = click.player();
    var homeName = home.name();

    if (type.isShiftClick()) {
      click.close();
      this.rename.prompt(player, homeName);
      return;
    }

    if (type == ClickType.DROP || type == ClickType.CONTROL_DROP) {
      openSubMenuFor(click, homeName, MaterialPickerMenu.ID, true);
      return;
    }

    if (type.isRightClick()) {
      openSubMenuFor(click, homeName, DeleteHomeDialog.ID, false);
      return;
    }

    click.close();

    var actor = this.framework.actorOf(player);
    this.teleporter.teleport(player, home, actor);
  }

  private void openSubMenuFor(
      @NonNull ClickContext click,
      @NonNull String homeName,
      @NonNull String menuId,
      boolean delay) {
    var player = click.player();
    var uuid = player.getUniqueId();

    this.target.set(uuid, homeName);

    if (delay) {
      click.close();
      this.scheduler.runOnEntityLater(
          player, () -> this.menus.open(player, menuId), SUBMENU_OPEN_DELAY);
      return;
    }

    click.open(menuId);
  }
}
