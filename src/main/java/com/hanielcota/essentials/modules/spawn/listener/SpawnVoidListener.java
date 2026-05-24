package com.hanielcota.essentials.modules.spawn.listener;

import com.hanielcota.essentials.modules.spawn.service.SpawnLocation;
import com.hanielcota.essentials.modules.spawn.service.SpawnService;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@RequiredArgsConstructor
public final class SpawnVoidListener implements Listener {

  private final SpawnService service;
  private final DelayedTeleport delayed;

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVoidDamage(@NonNull EntityDamageEvent event) {
    if (event.getCause() != DamageCause.VOID) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    var spawn = this.service.current().flatMap(SpawnLocation::resolve);
    if (spawn.isEmpty()) {
      return;
    }
    event.setCancelled(true);
    // Cancel any pending warm-up — DelayedTeleportCanceller runs at MONITOR with
    // ignoreCancelled=true and we just cancelled the damage, so it won't fire.
    // Without this, the rescue teleport would land first and the warm-up would
    // teleport the player back to wherever they were going seconds later.
    this.delayed.cancelAndNotify(player.getUniqueId());
    player.setFallDistance(0);
    player.teleport(spawn.get());
  }
}
