package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public final class BackJoinListener implements Listener {

  private final TeleportHistory history;

  @EventHandler
  public void onJoin(@NonNull PlayerJoinEvent event) {
    var player = event.getPlayer();
    var uuid = player.getUniqueId();
    var location = player.getLocation();

    this.history.push(uuid, location, TeleportHistory.Cause.TELEPORT);
  }
}
