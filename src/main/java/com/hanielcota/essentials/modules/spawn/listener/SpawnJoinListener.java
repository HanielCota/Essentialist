package com.hanielcota.essentials.modules.spawn.listener;

import com.hanielcota.essentials.modules.spawn.service.SpawnLocation;
import com.hanielcota.essentials.modules.spawn.service.SpawnService;
import io.papermc.paper.event.player.AsyncPlayerSpawnLocationEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class SpawnJoinListener implements Listener {

  private final SpawnService service;

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpawnLocation(@NonNull AsyncPlayerSpawnLocationEvent event) {
    var current = this.service.current();
    var resolved = current.flatMap(SpawnLocation::resolve);

    resolved.ifPresent(event::setSpawnLocation);
  }
}
