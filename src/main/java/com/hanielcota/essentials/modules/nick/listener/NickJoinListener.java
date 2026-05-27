package com.hanielcota.essentials.modules.nick.listener;

import com.hanielcota.essentials.modules.nick.service.NickApplier;
import com.hanielcota.essentials.modules.nick.service.NickService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Re-applies a stored nickname to the joiner's display + tab name. Without this the cache holds the
 * nick across restarts but the visible names reset to the real names when players reconnect.
 */
@RequiredArgsConstructor
public final class NickJoinListener implements Listener {

  private final NickService service;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(@NonNull PlayerJoinEvent event) {
    var player = event.getPlayer();
    var id = player.getUniqueId();

    var entry = this.service.nickOf(id).orElse(null);
    if (entry == null) {
      return;
    }

    NickApplier.apply(player, entry.nickname());
  }
}
