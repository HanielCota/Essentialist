package com.hanielcota.essentials.modules.enderchest.listener;

import java.util.ArrayList;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Closes open views into the quitting player's ender chest.
 *
 * <p>{@code /echest <player>} opens {@code target.getEnderChest()} — a live reference. If the
 * target logs out while a staff member is editing, every subsequent click mutates the cached
 * inventory; the target's next clean login persists the profile from the join state and silently
 * overwrites those edits, so the inserted items vanish.
 */
public final class EnderChestQuitListener implements Listener {

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var enderChest = event.getPlayer().getEnderChest();
    // Copy first — closeInventory mutates the viewer list while we iterate.
    for (var viewer : new ArrayList<>(enderChest.getViewers())) {
      viewer.closeInventory();
    }
  }
}
