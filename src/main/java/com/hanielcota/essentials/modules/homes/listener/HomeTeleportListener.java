package com.hanielcota.essentials.modules.homes.listener;

import com.hanielcota.essentials.modules.homes.menu.HomesActionTarget;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameSessions;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Captures the home-domain events and delegates to the right service.
 *
 * <ul>
 *   <li>{@link PlayerMoveEvent} — cancels any pending warm-up for the player via {@link
 *       DelayedTeleport#cancel(java.util.UUID)} when they cross a block boundary. The shared {@code
 *       DelayedTeleport} listener already does the same; this listener is the home module's own
 *       hook so future home-specific rules can land here without touching the shared service. The
 *       cancel call is idempotent (the second remove is a no-op).
 *   <li>{@link PlayerQuitEvent} — drops the player's entries from {@link HomesActionTarget} and
 *       {@link HomeRenameSessions} so they do not leak across reconnects.
 * </ul>
 */
@RequiredArgsConstructor
public final class HomeTeleportListener implements Listener {

  private final DelayedTeleport delayed;
  private final HomesActionTarget actionTarget;
  private final HomeRenameSessions renameSessions;

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMove(@NonNull PlayerMoveEvent event) {
    var from = event.getFrom();
    var to = event.getTo();

    if (from.getBlockX() == to.getBlockX()
        && from.getBlockY() == to.getBlockY()
        && from.getBlockZ() == to.getBlockZ()) {
      return;
    }

    var uuid = event.getPlayer().getUniqueId();
    if (delayed.isPending(uuid)) {
      delayed.cancel(uuid);
    }
  }

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var uuid = event.getPlayer().getUniqueId();

    actionTarget.clear(uuid);
    renameSessions.cancel(uuid);
  }
}
