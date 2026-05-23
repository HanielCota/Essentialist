package com.hanielcota.essentials.modules.invsee.service;

import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

/** Schedules the write-back of an /invsee GUI to its target player. */
@RequiredArgsConstructor
public final class InvseeSynchronizer {

  private final Scheduler scheduler;
  private final InvseeService service;

  // Syncs view back to its target next tick, once the current click/drag is applied.
  public void scheduleSync(@NonNull InvseeHolder holder, @NonNull Inventory view) {
    var target = Bukkit.getPlayer(holder.targetId());
    if (target == null) {
      return;
    }
    // Routed through the target's region: on Folia its inventory may only be touched there.
    this.scheduler.runOnEntity(
        target,
        () -> {
          // Skip when the target died meanwhile: writing a stale view onto an inventory
          // already emptied by death drops would duplicate items.
          if (!target.isDead()) {
            this.service.sync(target, view);
          }
        });
  }
}
