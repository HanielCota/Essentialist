package com.hanielcota.essentials.modules.ban.listener;

import com.hanielcota.essentials.modules.ban.menu.BanMenuState;
import com.hanielcota.essentials.modules.ban.service.BanNickSessions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/** Drops per-viewer ban-menu state and any pending nick session when a staff member disconnects. */
@RequiredArgsConstructor
public final class BanMenuCleanupListener implements Listener {

  private final BanMenuState state;
  private final BanNickSessions sessions;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var id = event.getPlayer().getUniqueId();

    this.state.clear(id);
    this.sessions.cancel(id);
  }
}
