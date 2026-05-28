package com.hanielcota.essentials.modules.combat.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.combat.config.CombatConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@RequiredArgsConstructor
public final class PvpListener implements Listener {

  private final ConfigHandle<CombatConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onPlayerDamage(@NonNull EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof Player victim)) {
      return;
    }

    var attacker = resolveAttacker(event.getDamager());
    if (attacker == null || attacker.equals(victim)) {
      return;
    }

    var snap = this.config.value();
    var worldName = victim.getWorld().getName();

    if (snap.pvp() || !snap.appliesTo(worldName)) {
      return;
    }
    if (snap.hasBypassPermission() && attacker.hasPermission(snap.bypassPermission())) {
      return;
    }

    event.setCancelled(true);
  }

  private static Player resolveAttacker(@NonNull Entity damager) {
    if (damager instanceof Player player) {
      return player;
    }
    if (!(damager instanceof Projectile projectile)) {
      return null;
    }

    var shooter = projectile.getShooter();
    return shooter instanceof Player shooterPlayer ? shooterPlayer : null;
  }
}
