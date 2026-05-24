package com.hanielcota.essentials.modules.teleport.listener;

import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class DelayedTeleportCanceller implements Listener {

  private final DelayedTeleport delayed;

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(@NonNull EntityDamageEvent event) {
    if (event.getEntity() instanceof Player player) {
      this.delayed.cancelAndNotify(player.getUniqueId());
    }
  }

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    this.delayed.cancel(event.getPlayer().getUniqueId());
  }
}
