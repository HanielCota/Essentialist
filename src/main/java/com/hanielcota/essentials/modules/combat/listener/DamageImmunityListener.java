package com.hanielcota.essentials.modules.combat.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.combat.config.CombatConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

@RequiredArgsConstructor
public final class DamageImmunityListener implements Listener {

  private final ConfigHandle<CombatConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerDamage(@NonNull EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    var snap = this.config.value();
    var worldName = player.getWorld().getName();
    var causeName = event.getCause().name();

    if (!snap.appliesTo(worldName)) {
      return;
    }
    if (!snap.isDamageCauseImmune(causeName)) {
      return;
    }

    event.setCancelled(true);
  }
}
