package com.hanielcota.essentials.modules.entity.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.entity.config.EntityConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

@RequiredArgsConstructor
public final class HangingProtectionListener implements Listener {

  private final ConfigHandle<EntityConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onHangingBreak(@NonNull HangingBreakEvent event) {
    var hanging = event.getEntity();
    var snap = this.config.value();
    var worldName = hanging.getWorld().getName();

    if (!isHangingProtected(snap, hanging) || !snap.appliesTo(worldName)) {
      return;
    }
    if (isBypassingRemover(snap, event)) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onItemFrameDamage(@NonNull EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof ItemFrame frame)) {
      return;
    }

    var snap = this.config.value();
    var worldName = frame.getWorld().getName();

    if (!snap.protectItemFrames() || !snap.appliesTo(worldName)) {
      return;
    }
    if (isBypassingPlayer(snap, event.getDamager())) {
      return;
    }

    event.setCancelled(true);
  }

  private static boolean isHangingProtected(@NonNull EntityConfig snap, @NonNull Entity hanging) {
    if (hanging instanceof ItemFrame) {
      return snap.protectItemFrames();
    }
    if (hanging instanceof Painting) {
      return snap.protectPaintings();
    }

    return false;
  }

  private static boolean isBypassingRemover(
      @NonNull EntityConfig snap, @NonNull HangingBreakEvent event) {
    if (!(event instanceof HangingBreakByEntityEvent byEntity)) {
      return false;
    }

    return isBypassingPlayer(snap, byEntity.getRemover());
  }

  private static boolean isBypassingPlayer(@NonNull EntityConfig snap, Entity entity) {
    if (!(entity instanceof Player player)) {
      return false;
    }

    return snap.hasBypassPermission() && player.hasPermission(snap.bypassPermission());
  }
}
