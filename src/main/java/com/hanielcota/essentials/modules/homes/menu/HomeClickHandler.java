package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.menu.options.HomeOptionsMenu;
import com.hanielcota.essentials.modules.homes.service.HomeTeleporter;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.inventory.ClickType;

/**
 * Routes a /homes menu click to one of two sub-flows by {@link ClickType}: a right-click stashes
 * the clicked home as the current {@link HomesActionTarget} and opens the per-home options sub-menu
 * (rename / change icon / delete / teleport), while every other click teleports directly via {@link
 * HomeTeleporter}. The shift-rename and Q-icon hotkeys are gone — all secondary actions live behind
 * the options menu so the UX is fully menu-driven and survives MenuFramework's "dangerous click
 * type" guard that hardcodes DROP/Q as blocked.
 */
@RequiredArgsConstructor
public final class HomeClickHandler {

  private final HomeTeleporter teleporter;
  private final ActorFactory actors;
  private final HomesActionTarget target;

  public void handle(@NonNull ClickContext click, @NonNull Home home) {
    var type = click.clickType();

    if (type.isRightClick()) {
      openOptions(click, home);
      return;
    }

    click.close();

    var actor = this.actors.actorOf(click.player());
    this.teleporter.teleport(click.player(), home, actor);
  }

  private void openOptions(@NonNull ClickContext click, @NonNull Home home) {
    var player = click.player();
    var uuid = player.getUniqueId();

    this.target.set(uuid, home.name());
    click.switchTo(HomeOptionsMenu.ID);
  }
}
