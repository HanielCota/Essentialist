package com.hanielcota.essentials.modules.environment.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.environment.config.EnvironmentConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;

@RequiredArgsConstructor
public final class IceSnowListener implements Listener {

  private final ConfigHandle<EnvironmentConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onFade(@NonNull BlockFadeEvent event) {
    var snap = this.config.value();
    var faded = event.getBlock().getType();
    var worldName = event.getBlock().getWorld().getName();

    if (!snap.appliesTo(worldName)) {
      return;
    }

    var iceMelt = isIce(faded) && snap.preventIceMelt();
    var snowMelt = isSnow(faded) && snap.preventSnowMelt();

    if (!iceMelt && !snowMelt) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onForm(@NonNull BlockFormEvent event) {
    var snap = this.config.value();
    var formed = event.getNewState().getType();
    var worldName = event.getBlock().getWorld().getName();

    if (!snap.appliesTo(worldName)) {
      return;
    }

    var iceForm = isIce(formed) && snap.preventIceForm();
    var snowForm = isSnow(formed) && snap.preventSnowForm();

    if (!iceForm && !snowForm) {
      return;
    }

    event.setCancelled(true);
  }

  private static boolean isIce(@NonNull Material material) {
    return material == Material.ICE || material == Material.FROSTED_ICE;
  }

  private static boolean isSnow(@NonNull Material material) {
    return material == Material.SNOW || material == Material.SNOW_BLOCK;
  }
}
