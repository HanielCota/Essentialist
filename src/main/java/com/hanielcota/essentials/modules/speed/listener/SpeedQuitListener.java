package com.hanielcota.essentials.modules.speed.listener;

import com.hanielcota.essentials.modules.speed.service.SpeedService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class SpeedQuitListener implements Listener {

  private final SpeedService service;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    this.service.resetIfModified(player);
  }
}
