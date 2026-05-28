package com.hanielcota.essentials.modules.environment.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.environment.config.EnvironmentConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockSpreadEvent;

@RequiredArgsConstructor
public final class FireProtectionListener implements Listener {

  private final ConfigHandle<EnvironmentConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onFireSpread(@NonNull BlockSpreadEvent event) {
    var source = event.getSource();
    var sourceType = source.getType();

    if (sourceType != Material.FIRE && sourceType != Material.SOUL_FIRE) {
      return;
    }

    var snap = this.config.value();
    var worldName = event.getBlock().getWorld().getName();

    if (!snap.preventFireSpread() || !snap.appliesTo(worldName)) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockBurn(@NonNull BlockBurnEvent event) {
    var snap = this.config.value();
    var worldName = event.getBlock().getWorld().getName();

    if (!snap.preventBlockBurn() || !snap.appliesTo(worldName)) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onIgnite(@NonNull BlockIgniteEvent event) {
    var snap = this.config.value();
    var worldName = event.getBlock().getWorld().getName();
    var causeName = event.getCause().name();

    if (!snap.preventIgnite() || !snap.appliesTo(worldName)) {
      return;
    }
    if (!snap.isIgniteCauseBlocked(causeName)) {
      return;
    }

    event.setCancelled(true);
  }
}
