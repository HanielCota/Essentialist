package com.hanielcota.essentials.modules.crops.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.crops.config.CropsConfig;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

@RequiredArgsConstructor
public final class ExplosionCropListener implements Listener {

  private final ConfigHandle<CropsConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onEntityExplode(@NonNull EntityExplodeEvent event) {
    var worldName = event.getLocation().getWorld().getName();
    protect(event.blockList(), worldName);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockExplode(@NonNull BlockExplodeEvent event) {
    var worldName = event.getBlock().getWorld().getName();
    protect(event.blockList(), worldName);
  }

  private void protect(@NonNull List<Block> blocks, @NonNull String worldName) {
    var snap = this.config.value();

    if (!snap.preventExplosion() && !snap.preventExplosionFarmland()) {
      return;
    }

    if (!snap.appliesTo(worldName)) {
      return;
    }

    blocks.removeIf(block -> isProtected(snap, block.getType()));
  }

  private static boolean isProtected(@NonNull CropsConfig snap, @NonNull Material type) {
    if (snap.preventExplosion() && Tag.CROPS.isTagged(type) && snap.isCropAllowed(type)) {
      return true;
    }

    return snap.preventExplosionFarmland() && type == Material.FARMLAND;
  }
}
