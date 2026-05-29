package com.hanielcota.essentials.modules.combat.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.combat.config.CombatConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

@RequiredArgsConstructor
public final class KeepOnDeathListener implements Listener {

  private final ConfigHandle<CombatConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDeath(@NonNull PlayerDeathEvent event) {
    var snap = this.config.value();
    var player = event.getEntity();
    var worldName = player.getWorld().getName();

    if (!snap.appliesTo(worldName)) {
      return;
    }

    if (snap.keepInventory()) {
      event.setKeepInventory(true);
      event.getDrops().clear();
    }

    if (snap.keepExperience()) {
      event.setKeepLevel(true);
      event.setDroppedExp(0);
    }
  }
}
