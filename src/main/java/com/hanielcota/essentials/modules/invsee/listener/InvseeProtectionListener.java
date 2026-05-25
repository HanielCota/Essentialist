package com.hanielcota.essentials.modules.invsee.listener;

import com.hanielcota.essentials.modules.invsee.service.InvseeHolder;
import com.hanielcota.essentials.modules.invsee.service.InvseeService;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
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
    var onlinePlayers = Bukkit.getOnlinePlayers();

    for (var viewer : onlinePlayers) {
      var openInventory = viewer.getOpenInventory();
      var topInventory = openInventory.getTopInventory();
      var topHolder = topInventory.getHolder();

      if (!(topHolder instanceof InvseeHolder holder)) {
        continue;
      }
      if (!holder.targetId().equals(targetId)) {
        continue;
      }

      viewer.closeInventory();
    }
  }

  @EventHandler
  public void onTargetQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var targetId = player.getUniqueId();

    closeViewsTargeting(targetId);
    this.service.releaseTarget(targetId);
  }

  @EventHandler
  public void onTargetDeath(@NonNull PlayerDeathEvent event) {
    var entity = event.getEntity();
    var targetId = entity.getUniqueId();

    closeViewsTargeting(targetId);
    // Mirror onTargetQuit — don't rely on each viewer's InventoryCloseEvent racing through the
    // event bus to free the lock. On Folia the close may be deferred until the viewer's region
    // tick, which would refuse a fresh /invsee against the same target in the meantime.
    this.service.releaseTarget(targetId);
  }

  @EventHandler
  public void onClose(@NonNull InventoryCloseEvent event) {
    var inventory = event.getInventory();
    var inventoryHolder = inventory.getHolder();

    if (!(inventoryHolder instanceof InvseeHolder holder)) {
      return;
    }

    var viewer = event.getPlayer();
    var viewerId = viewer.getUniqueId();
    var targetId = holder.targetId();

    this.service.release(targetId, viewerId);
  }
}
