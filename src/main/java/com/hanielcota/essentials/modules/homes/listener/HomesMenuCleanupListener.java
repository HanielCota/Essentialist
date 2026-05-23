package com.hanielcota.essentials.modules.homes.listener;

import com.hanielcota.essentials.modules.homes.menu.HomesMenu;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class HomesMenuCleanupListener implements Listener {

  private final HomesMenu menu;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var uuid = event.getPlayer().getUniqueId();
    this.menu.clearPrefetched(uuid);
  }
}
