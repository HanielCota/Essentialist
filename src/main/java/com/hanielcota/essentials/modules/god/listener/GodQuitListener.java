package com.hanielcota.essentials.modules.god.listener;

import com.hanielcota.essentials.modules.god.service.GodService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class GodQuitListener implements Listener {

  private final GodService service;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var id = player.getUniqueId();

    this.service.forget(id);
  }
}
