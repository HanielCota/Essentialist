package com.hanielcota.essentials.modules.environment.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.environment.config.EnvironmentConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

@RequiredArgsConstructor
public final class BucketRestrictionListener implements Listener {

  private final ConfigHandle<EnvironmentConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBucketEmpty(@NonNull PlayerBucketEmptyEvent event) {
    var snap = this.config.value();
    var player = event.getPlayer();
    var worldName = player.getWorld().getName();
    var bucket = event.getBucket();

    if (!snap.appliesTo(worldName)) {
      return;
    }
    if (snap.hasBypassPermission() && player.hasPermission(snap.bypassPermission())) {
      return;
    }

    var blockLava = bucket == Material.LAVA_BUCKET && snap.preventLavaBucket();
    var blockWater = bucket == Material.WATER_BUCKET && snap.preventWaterBucket();

    if (!blockLava && !blockWater) {
      return;
    }

    event.setCancelled(true);
  }
}
