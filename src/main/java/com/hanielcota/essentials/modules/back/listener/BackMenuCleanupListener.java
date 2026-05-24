package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.modules.back.menu.BackMenuState;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class BackMenuCleanupListener implements Listener {

  private final BackMenuState state;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    this.state.clear(event.getPlayer().getUniqueId());
  }
}
