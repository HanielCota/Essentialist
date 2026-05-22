package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import java.util.Objects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public final class PlayerTeleportListener implements Listener {

  private final TeleportHistory history;

  public PlayerTeleportListener(TeleportHistory history) {
    this.history = Objects.requireNonNull(history, "history");
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTeleport(PlayerTeleportEvent event) {
    if (isTrivial(event.getCause())) {
      return;
    }

    history.push(event.getPlayer().getUniqueId(), event.getFrom());
  }

  private static boolean isTrivial(TeleportCause cause) {
    return cause == TeleportCause.UNKNOWN
        || cause == TeleportCause.DISMOUNT
        || cause == TeleportCause.EXIT_BED;
  }
}
