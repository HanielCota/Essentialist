package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.modules.back.service.BackWarmupService;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class BackWarmupCanceller implements Listener {

  private final BackWarmupService warmup;

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(@NonNull EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player player)) {
      return;
    }

    var playerId = player.getUniqueId();
    this.warmup.cancel(playerId, true);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMove(@NonNull PlayerMoveEvent event) {
    var from = event.getFrom();
    var to = event.getTo();
    if (to == null) {
      return;
    }

    var sameWorld = Objects.equals(from.getWorld(), to.getWorld());
    var sameBlock =
        from.getBlockX() == to.getBlockX()
            && from.getBlockY() == to.getBlockY()
            && from.getBlockZ() == to.getBlockZ();

    if (sameWorld && sameBlock) {
      return;
    }

    var playerId = event.getPlayer().getUniqueId();
    this.warmup.cancel(playerId, true);
  }

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var playerId = event.getPlayer().getUniqueId();
    this.warmup.cancel(playerId, false);
  }
}
