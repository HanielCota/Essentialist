package com.hanielcota.essentials.modules.invsee.listener;

import com.hanielcota.essentials.modules.invsee.domain.InvseeHolder;
import com.hanielcota.essentials.modules.invsee.domain.InvseeLayout;
import com.hanielcota.essentials.modules.invsee.service.InvseeSynchronizer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

@RequiredArgsConstructor
public final class InvseeListener implements Listener {

  private static final String MODIFY_PERMISSION = "essentials.invsee.modify";

  private final InvseeSynchronizer synchronizer;

  // Run at MONITOR with ignoreCancelled=true so we only sync the view back to the target after the
  // event has run its full course AND survived every other plugin's cancel decision — otherwise an
  // anti-cheat / protection plugin cancelling the click would still trigger a writeback that
  // overwrites the live inventory with the stale view (item-loss / dupe vector).
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onClick(@NonNull InventoryClickEvent event) {
    var view = event.getView();
    var top = view.getTopInventory();
    var topHolder = top.getHolder();

    if (!(topHolder instanceof InvseeHolder holder)) {
      return;
    }

    var viewer = event.getWhoClicked();
    if (!viewer.hasPermission(MODIFY_PERMISSION)) {
      event.setCancelled(true);
      return;
    }

    var clickedInventory = event.getClickedInventory();
    var clickedSlot = event.getSlot();

    if (clickedInventory == top && clickedSlot >= InvseeLayout.FIRST_LOCKED_SLOT) {
      event.setCancelled(true);
      return;
    }

    this.synchronizer.scheduleSync(holder, top);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDrag(@NonNull InventoryDragEvent event) {
    var view = event.getView();
    var top = view.getTopInventory();
    var topHolder = top.getHolder();

    if (!(topHolder instanceof InvseeHolder holder)) {
      return;
    }

    var viewer = event.getWhoClicked();
    if (!viewer.hasPermission(MODIFY_PERMISSION)) {
      event.setCancelled(true);
      return;
    }

    var topSize = top.getSize();
    var rawSlots = event.getRawSlots();

    for (var rawSlot : rawSlots) {
      if (rawSlot < topSize && rawSlot >= InvseeLayout.FIRST_LOCKED_SLOT) {
        event.setCancelled(true);
        return;
      }
    }

    this.synchronizer.scheduleSync(holder, top);
  }
}
