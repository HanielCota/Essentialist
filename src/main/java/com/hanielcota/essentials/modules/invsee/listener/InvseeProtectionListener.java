package com.hanielcota.essentials.modules.invsee.listener;

import com.hanielcota.essentials.modules.invsee.service.InvseeHolder;
import com.hanielcota.essentials.modules.invsee.service.InvseeService;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Closes open /invsee views when their target leaves or dies, and releases the single-viewer lock
 * held by {@link InvseeService}.
 *
 * <p>Without the auto-close, a viewer would keep editing a stale inventory after the target logged
 * off, and editing it after a death — when the real inventory was already emptied into drops —
 * would duplicate items by writing the old contents back. Without the lock release, the next
 * `/invsee` against the same target would be refused indefinitely.
 */
@RequiredArgsConstructor
public final class InvseeProtectionListener implements Listener {

  private final InvseeService service;

  /** Closes every open /invsee GUI that mirrors the player with {@code targetId}. */
  private static void closeViewsTargeting(@NonNull UUID targetId) {
    for (var viewer : Bukkit.getOnlinePlayers()) {
      if (viewer.getOpenInventory().getTopInventory().getHolder() instanceof InvseeHolder holder
          && holder.targetId().equals(targetId)) {
        viewer.closeInventory();
      }
    }
  }

  @EventHandler
  public void onTargetQuit(@NonNull PlayerQuitEvent event) {
    var targetId = event.getPlayer().getUniqueId();
    closeViewsTargeting(targetId);
    this.service.releaseTarget(targetId);
  }

  @EventHandler
  public void onTargetDeath(@NonNull PlayerDeathEvent event) {
    closeViewsTargeting(event.getEntity().getUniqueId());
  }

  @EventHandler
  public void onClose(@NonNull InventoryCloseEvent event) {
    if (!(event.getInventory().getHolder() instanceof InvseeHolder holder)) {
      return;
    }
    HumanEntity viewer = event.getPlayer();
    this.service.release(holder.targetId(), viewer.getUniqueId());
  }
}
