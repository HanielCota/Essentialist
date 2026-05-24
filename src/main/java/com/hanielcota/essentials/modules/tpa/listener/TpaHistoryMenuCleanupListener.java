package com.hanielcota.essentials.modules.tpa.listener;

import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenuState;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class TpaHistoryMenuCleanupListener implements Listener {

  private final TpaHistoryMenuState state;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    this.state.clear(event.getPlayer().getUniqueId());
  }
}
