package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.util.Log;
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
    menus
        .open(player, menuId)
        .exceptionally(
            error -> {
              LOG.warn(error, "Menu {} failed to open for {}", menuId, player.getName());
              actor.sendError(OPEN_FAILURE);
              return null;
            });
  }
}
