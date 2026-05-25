package com.hanielcota.essentials.modules.spawn.listener;

import com.hanielcota.essentials.modules.spawn.domain.SpawnLocation;
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
    var current = this.service.current();
    var resolved = current.flatMap(SpawnLocation::resolve);

    resolved.ifPresent(event::setRespawnLocation);
  }
}
