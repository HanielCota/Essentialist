package com.hanielcota.essentials.modules.afk.listener;

import com.hanielcota.essentials.modules.afk.service.AfkService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Drops the quitter's AFK + activity entries so they rejoin clean. No exit broadcast — the server's
 * quit broadcast already covers the leave.
 */
@RequiredArgsConstructor
public final class AfkQuitListener implements Listener {

  private final AfkService service;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var id = event.getPlayer().getUniqueId();

    this.service.forget(id);
  }
}
