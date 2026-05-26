package com.hanielcota.essentials.modules.tpa.listener;

import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSessions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Drops per-session favorite state on quit so it does not leak across reconnects: any in-flight
 * favorite-add prompt and the action-menu selection.
 */
@RequiredArgsConstructor
public final class TpaFavoriteSessionCleanupListener implements Listener {

  private final TpaFavoriteSessions sessions;
  private final TpaFavoriteSelections selections;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var uuid = event.getPlayer().getUniqueId();

    this.sessions.cancel(uuid);
    this.selections.clear(uuid);
  }
}
