package com.hanielcota.essentials.modules.vanish.listener;

import com.hanielcota.essentials.modules.vanish.service.VanishService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Suppresses the quit broadcast for vanished players and drops their entry from the service.
 *
 * <p>State is memory-only by design: a vanished player who rejoins comes back visible. Cleaning up
 * on quit keeps the vanished set bounded to actually-online players.
 */
@RequiredArgsConstructor
public final class VanishQuitListener implements Listener {

  private final VanishService service;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var id = event.getPlayer().getUniqueId();
    if (this.service.exit(id)) {
      event.quitMessage(null);
    }
  }
}
