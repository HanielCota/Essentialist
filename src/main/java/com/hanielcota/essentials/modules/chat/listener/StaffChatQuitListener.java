package com.hanielcota.essentials.modules.chat.listener;

import com.hanielcota.essentials.modules.chat.service.StaffChatToggleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Clears the staff-chat toggle entry when a player disconnects. Prevents the in-memory set from
 * growing unbounded across reconnects and keeps the toggle scoped to a single session per the spec.
 */
@RequiredArgsConstructor
public final class StaffChatQuitListener implements Listener {

  private final StaffChatToggleService toggleService;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var playerId = player.getUniqueId();

    this.toggleService.clear(playerId);
  }
}
