package com.hanielcota.essentials.modules.vanish.listener;

import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.vanish.service.VanishTransitions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Rolls back vanish state on quit so the player rejoins clean.
 *
 * <p>{@code setInvulnerable} and {@code setCanPickupItems} are persisted to player NBT; without the
 * {@code unapply} call, a vanished player who quits stays invulnerable and unable to pick up items
 * across rejoins and server restarts. Quit broadcast is suppressed only when the player was
 * actually vanished. Runs at {@link EventPriority#HIGHEST} so any plugin composing the quit message
 * earlier has already finished.
 */
@RequiredArgsConstructor
public final class VanishQuitListener implements Listener {

  private final VanishService service;
  private final VanishTransitions transitions;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var id = player.getUniqueId();
    if (!this.service.isVanished(id)) {
      return;
    }

    this.transitions.leave(player);
    event.quitMessage(null);
  }
}
