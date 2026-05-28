package com.hanielcota.essentials.modules.crops.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.crops.CropsNotifier;
import com.hanielcota.essentials.modules.crops.config.CropsConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

@RequiredArgsConstructor
public final class FarmlandTrampleListener implements Listener {

  private final ConfigHandle<CropsConfig> config;
  private final CropsNotifier notifier;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onFarmlandTrample(@NonNull EntityChangeBlockEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    var block = event.getBlock();

    if (block.getType() != Material.FARMLAND) {
      return;
    }

    var snap = this.config.value();

    if (!snap.preventTrampling()) {
      return;
    }

    var worldName = block.getWorld().getName();

    if (!snap.appliesTo(worldName)) {
      return;
    }

    if (snap.hasBypassPermission() && player.hasPermission(snap.bypassPermission())) {
      return;
    }

    event.setCancelled(true);
    this.notifier.notifyTrampleBlocked(player);
  }
}
