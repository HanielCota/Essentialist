package com.hanielcota.essentials.modules.msg.listener;

import com.hanielcota.essentials.modules.msg.service.MsgService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Drops the quitting player's last-partner entry. The reverse side is left untouched — when the
 * partner uses /r afterwards, the command resolves the target as offline and surfaces a friendly
 * error instead of silently failing.
 */
@RequiredArgsConstructor
public final class MsgQuitListener implements Listener {

  private final MsgService partners;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var id = player.getUniqueId();

    this.partners.forget(id);
  }
}
