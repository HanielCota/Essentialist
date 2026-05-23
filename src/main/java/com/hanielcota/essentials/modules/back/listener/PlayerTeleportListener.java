package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@RequiredArgsConstructor
public final class PlayerTeleportListener implements Listener {

  private final TeleportHistory history;

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTeleport(PlayerTeleportEvent event) {
    var cause = event.getCause();

    if (cause == TeleportCause.UNKNOWN
        || cause == TeleportCause.DISMOUNT
        || cause == TeleportCause.EXIT_BED) {
      return;
    }

    history.push(event.getPlayer().getUniqueId(), event.getFrom());
  }
}
