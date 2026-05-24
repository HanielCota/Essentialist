package com.hanielcota.essentials.modules.info.listener;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.modules.info.menu.InfoMenu;
import com.hanielcota.essentials.modules.info.menu.InfoMenuState;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Clears the per-viewer {@link InfoMenuState} when the player closes the menu or quits.
 *
 * <p>Without the close hook, a viewer who ran {@code /informacoes <other>} and then closed the menu
 * would keep their {@code playerTarget} mapping forever, so a later {@code /informacoes} (without
 * args) would still resolve the previous target. The quit hook covers the disconnect-mid-menu path.
 */
@RequiredArgsConstructor
public final class InfoMenuCleanupListener implements Listener {

  private final InfoMenuState state;
  private final MenuService menus;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onClose(@NonNull InventoryCloseEvent event) {
    if (!(event.getPlayer() instanceof Player viewer)) {
      return;
    }
    var viewerId = viewer.getUniqueId();
    var session = this.menus.getSession(viewerId).orElse(null);
    if (session == null || !session.menuId().equals(InfoMenu.ID)) {
      return;
    }
    if (!session.isSameView(event.getView())) {
      return;
    }
    this.state.clear(viewerId);
  }

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    this.state.clear(event.getPlayer().getUniqueId());
  }
}
