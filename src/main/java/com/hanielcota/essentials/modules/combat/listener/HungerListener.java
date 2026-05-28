package com.hanielcota.essentials.modules.combat.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.combat.config.CombatConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

@RequiredArgsConstructor
public final class HungerListener implements Listener {

  private final ConfigHandle<CombatConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onFoodChange(@NonNull FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    // Only block decreases — eating to refill still works, the player just never gets hungrier.
    var newLevel = event.getFoodLevel();
    var currentLevel = player.getFoodLevel();

    if (newLevel >= currentLevel) {
      return;
    }

    var snap = this.config.value();
    var worldName = player.getWorld().getName();

    if (!snap.preventHunger() || !snap.appliesTo(worldName)) {
      return;
    }

    event.setCancelled(true);
  }
}
