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
    var cause = event.getCause();
    if (cause != DamageCause.VOID) {
      return;
    }
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    var current = this.service.current();
    var spawn = current.flatMap(SpawnLocation::resolve);
    if (spawn.isEmpty()) {
      return;
    }

    event.setCancelled(true);

    // Cancel any pending warm-up — DelayedTeleportCanceller runs at MONITOR with
    // ignoreCancelled=true and we just cancelled the damage, so it won't fire.
    // Without this, the rescue teleport would land first and the warm-up would
    // teleport the player back to wherever they were going seconds later.
    var playerId = player.getUniqueId();
    this.delayed.cancelAndNotify(playerId);

    player.setFallDistance(0);

    // teleportAsync so a cross-world spawn (e.g. nether-void → overworld spawn) does not stall the
    // damage event on chunk load. Discard the future — the rescue is fire-and-forget.
    var rescue = spawn.get();
    player.teleportAsync(rescue);
  }
}
