package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ItemClickHandler;
import com.hanielcota.essentials.modules.homes.service.Home;
import com.hanielcota.essentials.modules.homes.service.HomeTeleporter;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.ClickType;
import org.jspecify.annotations.NonNull;

/**
 * Routes a /homes menu click to one of four sub-flows by {@link ClickType}: LEFT teleports via
 * {@link HomeTeleporter}, RIGHT opens the delete dialog, SHIFT+click starts the chat rename, DROP
 * (Q) opens the material picker. The home-name sub-flows pre-populate {@link HomesActionTarget} so
 * the opened menu/dialog knows which home to act on.
 */
@RequiredArgsConstructor
public final class HomeClickHandler implements ItemClickHandler<Home> {

  private final HomeTeleporter teleporter;
  private final PaperCommandFramework framework;
  private final HomesActionTarget target;
  private final HomeRenamePrompter rename;

  @Override
  public void handle(@NonNull ClickContext click, @NonNull Home home) {
    var type = click.clickType();

    if (type.isShiftClick()) {
      click.close();
      rename.prompt(click.player(), home.name());
      return;
    }
    if (type == ClickType.DROP || type == ClickType.CONTROL_DROP) {
      openSubMenuFor(click, home, MaterialPickerMenu.ID);
      return;
    }
    if (type.isRightClick()) {
      openSubMenuFor(click, home, DeleteHomeDialog.ID);
      return;
    }

    click.close();
    teleporter.teleport(click.player(), home, framework.actorOf(click.player()));
  }

  private void openSubMenuFor(ClickContext click, Home home, String menuId) {
    target.set(click.player().getUniqueId(), home.name());
    click.open(menuId);
  }
}
