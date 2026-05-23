package com.hanielcota.essentials.modules.fly.listener;

import com.hanielcota.essentials.modules.fly.service.FlyService;
import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * Re-applies command-managed flight after a gamemode change, since switching to Survival or
 * Adventure resets {@code allowFlight} to the gamemode's default. The actual write is deferred to
 * the next tick on the player's region because the gamemode change is committed after this event
 * dispatch.
 */
@RequiredArgsConstructor
public final class FlyGameModeListener implements Listener {

  private final Scheduler scheduler;
  private final FlyService service;

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onGameModeChange(PlayerGameModeChangeEvent event) {
    GameMode next = event.getNewGameMode();
    if (next == GameMode.CREATIVE || next == GameMode.SPECTATOR) {
      return;
    }
    Player player = event.getPlayer();
    if (!service.isEnabled(player)) {
      return;
    }
    scheduler.runOnEntity(player, () -> player.setAllowFlight(true));
  }
}
