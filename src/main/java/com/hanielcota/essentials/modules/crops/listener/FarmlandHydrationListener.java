package com.hanielcota.essentials.modules.crops.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.crops.config.CropsConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

@RequiredArgsConstructor
public final class FarmlandHydrationListener implements Listener {

  private final ConfigHandle<CropsConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockFade(@NonNull BlockFadeEvent event) {
    var block = event.getBlock();

    if (block.getType() != Material.FARMLAND) {
      return;
    }

    var snap = this.config.value();

    if (!snap.permanentHydration()) {
      return;
    }

    var worldName = block.getWorld().getName();

    if (!snap.appliesTo(worldName)) {
      return;
    }

    event.setCancelled(true);
  }
}
