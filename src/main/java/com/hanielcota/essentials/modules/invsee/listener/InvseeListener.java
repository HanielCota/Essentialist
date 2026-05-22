package com.hanielcota.essentials.modules.invsee.listener;

import com.hanielcota.essentials.modules.invsee.menu.InvseeHolder;
import com.hanielcota.essentials.modules.invsee.service.InvseeService;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public final class InvseeListener implements Listener {

  private final Plugin plugin;
  private final InvseeService service;

  public InvseeListener(Plugin plugin, InvseeService service) {
    this.plugin = Objects.requireNonNull(plugin, "plugin");
    this.service = Objects.requireNonNull(service, "service");
  }

  @EventHandler
  public void onClick(InventoryClickEvent event) {
    Inventory top = event.getView().getTopInventory();
    if (!(top.getHolder() instanceof InvseeHolder holder)) {
      return;
    }
    if (event.getClickedInventory() == top && event.getSlot() >= InvseeService.FIRST_LOCKED_SLOT) {
      event.setCancelled(true);
      return;
    }
    scheduleSync(holder, top);
  }

  @EventHandler
  public void onDrag(InventoryDragEvent event) {
    Inventory top = event.getView().getTopInventory();
    if (!(top.getHolder() instanceof InvseeHolder holder)) {
      return;
    }
    for (int rawSlot : event.getRawSlots()) {
      if (rawSlot < top.getSize() && rawSlot >= InvseeService.FIRST_LOCKED_SLOT) {
        event.setCancelled(true);
        return;
      }
    }
    scheduleSync(holder, top);
  }

  /** Syncs the GUI back to the target next tick, once the click/drag has been applied. */
  private void scheduleSync(InvseeHolder holder, Inventory view) {
    Bukkit.getScheduler()
        .runTask(
            plugin,
            () -> {
              Player target = Bukkit.getPlayer(holder.targetId());
              if (target != null) {
                service.sync(target, view);
              }
            });
  }
}
