package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import lombok.NonNull;
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
  public void onTeleport(@NonNull PlayerTeleportEvent event) {
    var cause = event.getCause();

    if (cause == TeleportCause.UNKNOWN) {
      return;
    }
    if (cause == TeleportCause.DISMOUNT) {
      return;
    }
    if (cause == TeleportCause.EXIT_BED) {
      return;
    }
    // Staff toggling between players in spectator mode would otherwise pollute
    // /back history with every spectated point.
    if (cause == TeleportCause.SPECTATE) {
      return;
    }

    var player = event.getPlayer();
    var uuid = player.getUniqueId();
    var originLocation = event.getFrom();

    this.history.push(uuid, originLocation);
  }
}
