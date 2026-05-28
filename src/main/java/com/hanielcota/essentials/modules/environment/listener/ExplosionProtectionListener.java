package com.hanielcota.essentials.modules.environment.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.environment.config.EnvironmentConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

@RequiredArgsConstructor
public final class ExplosionProtectionListener implements Listener {

  private final ConfigHandle<EnvironmentConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityExplode(@NonNull EntityExplodeEvent event) {
    var snap = this.config.value();
    var worldName = event.getLocation().getWorld().getName();
    var sourceType = event.getEntityType().name();

    if (!snap.preventExplosionBlockDamage() || !snap.appliesTo(worldName)) {
      return;
    }
    if (!snap.isExplosionSourceBlocked(sourceType)) {
      return;
    }

    event.blockList().clear();
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockExplode(@NonNull BlockExplodeEvent event) {
    var snap = this.config.value();
    var worldName = event.getBlock().getWorld().getName();

    if (!snap.preventExplosionBlockDamage() || !snap.appliesTo(worldName)) {
      return;
    }

    event.blockList().clear();
  }
}
