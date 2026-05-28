package com.hanielcota.essentials.modules.tpa.listener;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.modules.tpa.menu.TpaPickPlayerMenu;
import com.hanielcota.essentials.modules.tpa.service.selection.TpaPickPlayerFilters;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

@RequiredArgsConstructor
public final class TpaPickPlayerFilterCleanupListener implements Listener {

  private final TpaPickPlayerFilters filters;
  private final MenuService menus;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onClose(@NonNull InventoryCloseEvent event) {
    if (!(event.getPlayer() instanceof Player viewer)) {
      return;
    }

    var viewerId = viewer.getUniqueId();
    var session = this.menus.getSession(viewerId).orElse(null);
    if (session == null || !TpaPickPlayerMenu.ID.equals(session.menuId())) {
      return;
    }
    if (!session.isSameView(event.getView())) {
      return;
    }

    this.filters.clear(viewerId);
  }
}
