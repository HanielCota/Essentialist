package com.hanielcota.essentials.modules.leaves.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.leaves.config.LeavesConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.LeavesDecayEvent;

@RequiredArgsConstructor
public final class LeafDecayListener implements Listener {

  private final ConfigHandle<LeavesConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onLeavesDecay(@NonNull LeavesDecayEvent event) {
    var snap = this.config.value();
    var block = event.getBlock();
    var worldName = block.getWorld().getName();

    if (!snap.appliesTo(worldName)) {
      return;
    }

    event.setCancelled(true);
  }
}
