package com.hanielcota.essentials.modules.info.listener;

import com.hanielcota.essentials.modules.info.menu.InfoMenuState;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class InfoMenuCleanupListener implements Listener {

  private final InfoMenuState state;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    this.state.clear(event.getPlayer().getUniqueId());
  }
}
