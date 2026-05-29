package com.hanielcota.essentials.modules.god.listener;

import com.hanielcota.essentials.modules.god.service.GodService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/** Cancels every incoming damage for players in god mode — the actual invulnerability. */
@RequiredArgsConstructor
public final class GodDamageListener implements Listener {

  private final GodService service;

  @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
  public void onDamage(@NonNull EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    var id = player.getUniqueId();
    if (!this.service.isGod(id)) {
      return;
    }

    event.setCancelled(true);
  }
}
