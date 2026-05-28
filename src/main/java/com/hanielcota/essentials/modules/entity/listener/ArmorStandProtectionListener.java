package com.hanielcota.essentials.modules.entity.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.entity.config.EntityConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

@RequiredArgsConstructor
public final class ArmorStandProtectionListener implements Listener {

  private final ConfigHandle<EntityConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onManipulate(@NonNull PlayerArmorStandManipulateEvent event) {
    var player = event.getPlayer();
    var worldName = player.getWorld().getName();

    if (!isProtectedAndEnforced(worldName, player)) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onDamage(@NonNull EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof ArmorStand stand)) {
      return;
    }

    var worldName = stand.getWorld().getName();

    if (!isProtectedAndEnforced(worldName, event.getDamager())) {
      return;
    }

    event.setCancelled(true);
  }

  private boolean isProtectedAndEnforced(@NonNull String worldName, Entity actor) {
    var snap = this.config.value();

    if (!snap.protectArmorStands() || !snap.appliesTo(worldName)) {
      return false;
    }
    if (actor instanceof Player player
        && snap.hasBypassPermission()
        && player.hasPermission(snap.bypassPermission())) {
      return false;
    }

    return true;
  }
}
