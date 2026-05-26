package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.hanielcota.essentials.shared.Log;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MenuOpenings {

  private static final Log LOG = Log.of(MenuOpenings.class);
  private static final String OPEN_FAILURE = "Não foi possível abrir o menu.";

  public static void open(
      @NonNull MenuService menus,
      @NonNull Player player,
      @NonNull String menuId,
      @NonNull CommandActor actor) {
    var openFuture = menus.open(player, menuId);

    openFuture.exceptionally(error -> handleOpenFailure(error, menuId, player, actor));
  }

  // Returns null typed as MenuSession to match the CompletableFuture<MenuSession> contract —
  // an open failure has no session to recover with, just feedback for the actor.
  private static MenuSession handleOpenFailure(
      @NonNull Throwable error,
      @NonNull String menuId,
      @NonNull Player player,
      @NonNull CommandActor actor) {
    var playerName = player.getName();

    LOG.warn(error, "Menu {} failed to open for {}", menuId, playerName);
    actor.sendError(OPEN_FAILURE);

    return null;
  }
}
