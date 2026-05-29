package com.hanielcota.essentials.modules.crops.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.crops.command.CropsNotifier;
import com.hanielcota.essentials.modules.crops.config.CropsConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Tag;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

@RequiredArgsConstructor
public final class CropBreakListener implements Listener {

  private final ConfigHandle<CropsConfig> config;
  private final CropsNotifier notifier;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBlockBreak(@NonNull BlockBreakEvent event) {
    var block = event.getBlock();
    var type = block.getType();

    if (!Tag.CROPS.isTagged(type)) {
      return;
    }

    var snap = this.config.value();

    if (!snap.preventBreak() || !snap.isCropAllowed(type)) {
      return;
    }

    var worldName = block.getWorld().getName();

    if (!snap.appliesTo(worldName)) {
      return;
    }

    var blockData = block.getBlockData();

    if (blockData instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {
      return;
    }

    var player = event.getPlayer();

    if (snap.hasBypassPermission() && player.hasPermission(snap.bypassPermission())) {
      return;
    }

    event.setCancelled(true);
    this.notifier.notifyBreakBlocked(player);
  }
}
