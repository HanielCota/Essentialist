package com.hanielcota.essentials.modules.entity.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.entity.config.EntityConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

@RequiredArgsConstructor
public final class CreatureSpawnListener implements Listener {

  private final ConfigHandle<EntityConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onCreatureSpawn(@NonNull CreatureSpawnEvent event) {
    var snap = this.config.value();
    var worldName = event.getEntity().getWorld().getName();
    var reasonName = event.getSpawnReason().name();
    var typeName = event.getEntityType().name();

    if (!snap.preventMobSpawns() || !snap.appliesTo(worldName)) {
      return;
    }
    if (!snap.isSpawnReasonBlocked(reasonName) || !snap.isSpawnTypeBlocked(typeName)) {
      return;
    }

    event.setCancelled(true);
  }
}
