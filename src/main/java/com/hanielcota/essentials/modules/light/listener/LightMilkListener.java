package com.hanielcota.essentials.modules.light.listener;

import com.hanielcota.essentials.modules.light.service.LightService;
import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;

/**
 * Re-applies command-managed night vision after the player drinks a milk bucket, which Bukkit uses
 * to wipe all active potion effects. The vanilla wipe runs after this event, so the re-apply is
 * deferred to the next entity tick.
 */
@RequiredArgsConstructor
public final class LightMilkListener implements Listener {

  private final Scheduler scheduler;
  private final LightService service;

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onConsume(@NonNull PlayerItemConsumeEvent event) {
    if (event.getItem().getType() != Material.MILK_BUCKET) {
      return;
    }
    var player = event.getPlayer();
    if (!this.service.isEnabled(player)) {
      return;
    }
    this.scheduler.runOnEntity(player, () -> this.service.reapply(player));
  }
}
