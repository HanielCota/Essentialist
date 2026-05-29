package com.hanielcota.essentials.modules.environment.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.environment.config.EnvironmentConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

@RequiredArgsConstructor
public final class FluidFlowListener implements Listener {

  private final ConfigHandle<EnvironmentConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onFluidFlow(@NonNull BlockFromToEvent event) {
    var snap = this.config.value();
    var fluidType = event.getBlock().getType();
    var worldName = event.getBlock().getWorld().getName();

    if (!snap.appliesTo(worldName)) {
      return;
    }

    var blockWater = fluidType == Material.WATER && snap.preventWaterFlow();
    var blockLava = fluidType == Material.LAVA && snap.preventLavaFlow();

    if (!blockWater && !blockLava) {
      return;
    }

    event.setCancelled(true);
  }
}
