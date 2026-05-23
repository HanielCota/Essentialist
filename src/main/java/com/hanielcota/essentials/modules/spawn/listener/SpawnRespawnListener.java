package com.hanielcota.essentials.modules.spawn.listener;

import com.hanielcota.essentials.modules.spawn.service.SpawnLocation;
import com.hanielcota.essentials.modules.spawn.service.SpawnService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

@RequiredArgsConstructor
public final class SpawnRespawnListener implements Listener {

  private final SpawnService service;

  @EventHandler(priority = EventPriority.HIGH)
  public void onRespawn(@NonNull PlayerRespawnEvent event) {
    this.service.current().flatMap(SpawnLocation::resolve).ifPresent(event::setRespawnLocation);
  }
}
