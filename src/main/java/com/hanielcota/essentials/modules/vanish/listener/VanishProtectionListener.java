package com.hanielcota.essentials.modules.vanish.listener;

import com.hanielcota.essentials.modules.vanish.service.VanishService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Enforces vanish invariants that {@code hidePlayer} and {@code setInvulnerable} do not cover.
 *
 * <p>Mob AI still sees vanished players, so {@link EntityTargetEvent} is cancelled to sever
 * pursuit. Outgoing damage from a vanished player would reveal them (a damage indicator from
 * "nothing" is the classic vanish-PvP exploit), so any damage they deal — melee or via a projectile
 * they shot — is cancelled.
 */
@RequiredArgsConstructor
public final class VanishProtectionListener implements Listener {

  private final VanishService service;

  @EventHandler(ignoreCancelled = true)
  public void onTarget(@NonNull EntityTargetEvent event) {
    if (!(event.getTarget() instanceof Player target)) {
      return;
    }

    var targetId = target.getUniqueId();
    if (!this.service.isVanished(targetId)) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(ignoreCancelled = true)
  public void onDamage(@NonNull EntityDamageByEntityEvent event) {
    var damager = event.getDamager();
    var attacker = resolveAttacker(damager);
    if (attacker == null) {
      return;
    }

    var attackerId = attacker.getUniqueId();
    if (!this.service.isVanished(attackerId)) {
      return;
    }

    event.setCancelled(true);
  }

  private static Player resolveAttacker(@NonNull Entity damager) {
    if (damager instanceof Player player) {
      return player;
    }
    if (damager instanceof Projectile projectile
        && projectile.getShooter() instanceof Player shooter) {
      return shooter;
    }
    return null;
  }
}
