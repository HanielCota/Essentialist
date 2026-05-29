package com.hanielcota.essentials.modules.essentials.listener;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.modules.essentials.menu.EssentialsModulesMenu;
import com.hanielcota.essentials.modules.essentials.menu.EssentialsModulesMenuState;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/** Clears the per-viewer menu state when the module-control menu is closed or the player quits. */
@RequiredArgsConstructor
public final class ModulesMenuCleanupListener implements Listener {

  private final EssentialsModulesMenuState state;
  private final MenuService menus;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onClose(@NonNull InventoryCloseEvent event) {
    var human = event.getPlayer();
    if (!(human instanceof Player viewer)) {
      return;
    }

    var viewerId = viewer.getUniqueId();
    var sessionOpt = this.menus.getSession(viewerId);
    var session = sessionOpt.orElse(null);
    if (session == null) {
      return;
    }

    var menuId = session.menuId();
    if (!menuId.equals(EssentialsModulesMenu.ID)) {
      return;
    }

    var view = event.getView();
    if (!session.isSameView(view)) {
      return;
    }

    this.state.clear(viewerId);
  }

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var quitter = event.getPlayer();
    var viewerId = quitter.getUniqueId();

    this.state.clear(viewerId);
  }
}
