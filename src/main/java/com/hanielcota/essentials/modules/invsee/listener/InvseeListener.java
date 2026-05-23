package com.hanielcota.essentials.modules.invsee.listener;

import com.hanielcota.essentials.modules.invsee.service.InvseeHolder;
import com.hanielcota.essentials.modules.invsee.service.InvseeService;
import com.hanielcota.essentials.modules.invsee.service.InvseeSynchronizer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

@RequiredArgsConstructor
public final class InvseeListener implements Listener {

  private final InvseeSynchronizer synchronizer;

  @EventHandler
  public void onClick(@NonNull InventoryClickEvent event) {
    var top = event.getView().getTopInventory();
    if (!(top.getHolder() instanceof InvseeHolder holder)) {
      return;
    }
    if (event.getClickedInventory() == top && event.getSlot() >= InvseeService.FIRST_LOCKED_SLOT) {
      event.setCancelled(true);
      return;
    }
    this.synchronizer.scheduleSync(holder, top);
  }

  @EventHandler
  public void onDrag(@NonNull InventoryDragEvent event) {
    var top = event.getView().getTopInventory();
    if (!(top.getHolder() instanceof InvseeHolder holder)) {
      return;
    }
    for (var rawSlot : event.getRawSlots()) {
      if (rawSlot < top.getSize() && rawSlot >= InvseeService.FIRST_LOCKED_SLOT) {
        event.setCancelled(true);
        return;
      }
    }
    this.synchronizer.scheduleSync(holder, top);
  }
}
