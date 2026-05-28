package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.modules.back.service.BackPrefetch;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class BackMenuCleanupListener implements Listener {

  private final BackPrefetch prefetch;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var playerId = player.getUniqueId();

    this.prefetch.clear(playerId);
  }
}
