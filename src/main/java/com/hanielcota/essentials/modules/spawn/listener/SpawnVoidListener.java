package com.hanielcota.essentials.modules.spawn.listener;

import com.hanielcota.essentials.modules.spawn.service.SpawnLocation;
import com.hanielcota.essentials.modules.spawn.service.SpawnService;
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

  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onVoidDamage(EntityDamageEvent event) {
    if (event.getCause() != DamageCause.VOID) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }
    var spawn = service.current().flatMap(SpawnLocation::resolve);
    if (spawn.isEmpty()) {
      return;
    }
    event.setCancelled(true);
    player.setFallDistance(0);
    player.teleport(spawn.get());
  }
}
