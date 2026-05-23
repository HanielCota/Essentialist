package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

@RequiredArgsConstructor
public final class PlayerDeathListener implements Listener {

  private final TeleportHistory history;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDeath(@NonNull PlayerDeathEvent event) {
    var player = event.getEntity();
    var location = player.getLocation();
    var uuid = player.getUniqueId();

    this.history.push(uuid, location);
  }
}
