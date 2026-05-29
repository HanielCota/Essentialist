package com.hanielcota.essentials.modules.kit.listener;

import com.hanielcota.essentials.modules.kit.menu.KitMenuState;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/** Drops a player's kit-menu navigation state on quit. */
@RequiredArgsConstructor
public final class KitMenuCleanupListener implements Listener {

  private final KitMenuState state;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var uuid = event.getPlayer().getUniqueId();

    this.state.clear(uuid);
  }
}
