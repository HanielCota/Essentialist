package com.hanielcota.essentials.modules.entity.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.entity.config.EntityConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;

@RequiredArgsConstructor
public final class ItemDespawnListener implements Listener {

  private final ConfigHandle<EntityConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onItemDespawn(@NonNull ItemDespawnEvent event) {
    var snap = this.config.value();
    var worldName = event.getEntity().getWorld().getName();

    if (!snap.preventItemDespawn() || !snap.appliesTo(worldName)) {
      return;
    }

    event.setCancelled(true);
  }
}
