package com.hanielcota.essentials.modules.light.listener;

import com.hanielcota.essentials.modules.light.service.LightService;
import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Re-applies command-managed night vision after a respawn, since the player's potion effects are
 * cleared on death. The effect is scheduled on the next entity tick so it lands after Bukkit has
 * finished rebuilding the post-respawn player state.
 */
@RequiredArgsConstructor
public final class LightRespawnListener implements Listener {

  private final Scheduler scheduler;
  private final LightService service;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();
    if (!service.isEnabled(player)) {
      return;
    }
    scheduler.runOnEntity(player, () -> service.reapply(player));
  }
}
