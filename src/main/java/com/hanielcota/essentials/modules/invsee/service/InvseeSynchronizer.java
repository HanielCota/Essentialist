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
    var targetId = holder.targetId();
    var target = Bukkit.getPlayer(targetId);

    if (target == null) {
      return;
    }

    // Routed through the target's region: on Folia its inventory may only be touched there.
    // Re-resolve inside the callback — `target` was captured here on the click thread and the
    // player can quit between scheduling and execution. Resolving by UUID at run time gives us
    // the live entity (or null if they're gone).
    Runnable writeBack =
        () -> {
          var live = Bukkit.getPlayer(targetId);
          if (live == null || !live.isOnline() || live.isDead()) {
            return;
          }
          this.service.sync(live, view);
        };

    this.scheduler.runOnEntity(target, writeBack);
  }
}
