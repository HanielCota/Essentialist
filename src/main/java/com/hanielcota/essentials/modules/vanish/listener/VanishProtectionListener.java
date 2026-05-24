package com.hanielcota.essentials.modules.vanish.listener;

import com.hanielcota.essentials.modules.vanish.service.VanishService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

/**
 * Stops mobs from acquiring vanished players as targets.
 *
 * <p>{@code Player#hidePlayer} hides the entity from clients and the tab list, but mob AI keeps
 * seeing the vanished player and will walk towards them. {@code setInvulnerable} on the player
 * stops damage but not pursuit. Cancelling the target event severs both the visual giveaway and the
 * AI lock.
 */
@RequiredArgsConstructor
public final class VanishProtectionListener implements Listener {

  private final VanishService service;

  @EventHandler(ignoreCancelled = true)
  public void onTarget(@NonNull EntityTargetEvent event) {
    if (!(event.getTarget() instanceof Player target)) {
      return;
    }
    if (this.service.isVanished(target.getUniqueId())) {
      event.setCancelled(true);
    }
  }
}
