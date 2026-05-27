package com.hanielcota.essentials.modules.tpa.listener;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.modules.tpa.menu.TpaTargetActionMenu;
import com.hanielcota.essentials.modules.tpa.service.TpaTargetSelections;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Drops the per-viewer target-action selection when the menu is closed by any means (clicking
 * close, Escape, switching to another menu) and when the player quits, so it does not leak across
 * reconnects or stale opens.
 */
@RequiredArgsConstructor
public final class TpaTargetSelectionCleanupListener implements Listener {

  private final TpaTargetSelections selections;
  private final MenuService menus;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onClose(@NonNull InventoryCloseEvent event) {
    if (!(event.getPlayer() instanceof Player viewer)) {
      return;
    }

    var viewerId = viewer.getUniqueId();
    var session = this.menus.getSession(viewerId).orElse(null);
    if (session == null || !TpaTargetActionMenu.ID.equals(session.menuId())) {
      return;
    }
    if (!session.isSameView(event.getView())) {
      return;
    }

    this.selections.clear(viewerId);
  }

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    this.selections.clear(event.getPlayer().getUniqueId());
  }
}
