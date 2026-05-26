package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameOrchestrator;
import com.hanielcota.essentials.modules.homes.service.HomeTeleporter;
import com.hanielcota.essentials.paper.ActorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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
  private final List<Route> routes = buildRoutes();

  private List<Route> buildRoutes() {
    var list = new ArrayList<Route>();
    list.add(new Route(ClickType::isShiftClick, this::handleRename));
    list.add(
        new Route(
            type -> type == ClickType.DROP || type == ClickType.CONTROL_DROP,
            (click, home) -> openSubMenuFor(click, home.name(), MaterialCategoryMenu.ID)));
    list.add(
        new Route(
            ClickType::isRightClick,
            (click, home) -> openSubMenuFor(click, home.name(), DeleteHomeDialog.ID)));
    return List.copyOf(list);
  }

  public void handle(@NonNull ClickContext click, @NonNull Home home) {
    var type = click.clickType();

    for (var route : this.routes) {
      if (route.predicate.test(type)) {
        route.action.handle(click, home);
        return;
      }
    }

    click.close();

    var actor = this.actors.actorOf(click.player());
    this.teleporter.teleport(click.player(), home, actor);
  }

  private void handleRename(@NonNull ClickContext click, @NonNull Home home) {
    click.close();
    this.rename.prompt(click.player(), home.name());
  }

  private void openSubMenuFor(
      @NonNull ClickContext click, @NonNull String homeName, @NonNull String menuId) {
    var player = click.player();
    var uuid = player.getUniqueId();

    this.target.set(uuid, homeName);
    click.switchTo(menuId);
  }

  private record Route(@NonNull Predicate<ClickType> predicate, @NonNull HomeAction action) {}

  @FunctionalInterface
  private interface HomeAction {
    void handle(@NonNull ClickContext click, @NonNull Home home);
  }
}
