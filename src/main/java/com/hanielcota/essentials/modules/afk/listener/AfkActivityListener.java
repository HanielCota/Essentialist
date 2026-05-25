package com.hanielcota.essentials.modules.afk.listener;

import com.hanielcota.essentials.modules.afk.service.AfkService;
import com.hanielcota.essentials.modules.afk.service.AfkTransitions;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Records activity timestamps for the auto checker and un-AFKs the player on any meaningful
 * activity. Activity sources: position-changing movement (look-only deltas ignored), interactions,
 * and chat. Commands intentionally do not count — they would force a special-case for {@code /afk}
 * itself.
 */
@RequiredArgsConstructor
public final class AfkActivityListener implements Listener {

  private final AfkService service;
  private final AfkTransitions transitions;

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMove(@NonNull PlayerMoveEvent event) {
    var from = event.getFrom();
    var to = event.getTo();
    if (to == null) {
      return;
    }
    if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
      return;
    }

    record(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInteract(@NonNull PlayerInteractEvent event) {
    record(event.getPlayer());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChat(@NonNull AsyncChatEvent event) {
    record(event.getPlayer());
  }

  private void record(@NonNull Player player) {
    var id = player.getUniqueId();
    var now = System.currentTimeMillis();
    var name = player.getName();

    this.service.recordActivity(id, now);
    this.transitions.exit(id, name);
  }
}
