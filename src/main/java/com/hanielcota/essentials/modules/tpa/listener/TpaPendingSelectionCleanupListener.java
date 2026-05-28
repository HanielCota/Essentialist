package com.hanielcota.essentials.modules.tpa.listener;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.modules.tpa.menu.pending.TpaPendingActionMenu;
import com.hanielcota.essentials.modules.tpa.menu.pending.TpaPendingMenu;
import com.hanielcota.essentials.modules.tpa.service.selection.TpaPendingSelections;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

@RequiredArgsConstructor
public final class TpaPendingSelectionCleanupListener implements Listener {

  private static final Set<String> TRACKED_MENUS =
      Set.of(TpaPendingMenu.ID, TpaPendingActionMenu.ID);

  private final TpaPendingSelections selections;
  private final MenuService menus;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onClose(@NonNull InventoryCloseEvent event) {
    if (!(event.getPlayer() instanceof Player viewer)) {
      return;
    }

    var viewerId = viewer.getUniqueId();
    var session = this.menus.getSession(viewerId).orElse(null);
    if (session == null || !TRACKED_MENUS.contains(session.menuId())) {
      return;
    }
    if (!session.isSameView(event.getView())) {
      return;
    }

    this.selections.clear(viewerId);
  }
}
