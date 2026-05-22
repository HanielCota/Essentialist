package com.hanielcota.essentials.modules.invsee.listener;

import com.hanielcota.essentials.modules.invsee.service.InvseeHolder;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Closes open /invsee views when their target leaves or dies.
 *
 * <p>Without this, a viewer would keep editing a stale inventory after the target logged off, and
 * editing it after a death — when the real inventory was already emptied into drops — would
 * duplicate items by writing the old contents back.
 */
public final class InvseeProtectionListener implements Listener {

  /** Closes every open /invsee GUI that mirrors the player with {@code targetId}. */
  private static void closeViewsTargeting(UUID targetId) {
    for (Player viewer : Bukkit.getOnlinePlayers()) {
      if (viewer.getOpenInventory().getTopInventory().getHolder() instanceof InvseeHolder holder
          && holder.targetId().equals(targetId)) {
        viewer.closeInventory();
      }
    }
  }

  @EventHandler
  public void onTargetQuit(PlayerQuitEvent event) {
    closeViewsTargeting(event.getPlayer().getUniqueId());
  }

  @EventHandler
  public void onTargetDeath(PlayerDeathEvent event) {
    closeViewsTargeting(event.getEntity().getUniqueId());
  }
}
