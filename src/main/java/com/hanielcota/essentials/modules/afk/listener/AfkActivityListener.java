package com.hanielcota.essentials.modules.afk.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.afk.config.AfkConfig;
import com.hanielcota.essentials.modules.afk.service.AfkService;
import com.hanielcota.essentials.modules.afk.service.AfkTransitions;
import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Records activity timestamps for the auto checker and un-AFKs the player on any meaningful
 * activity. Activity sources: position-changing movement (look-only deltas ignored), interactions,
 * and chat. Commands intentionally do not count — they would force a special-case for {@code /afk}
 * itself.
 *
 * <p>{@link PlayerMoveEvent} fires up to 20× per second per player; debouncing per-player to the
 * configured interval keeps the activity write cost off the hot path without losing the "user is
 * moving" signal.
 */
@RequiredArgsConstructor
public final class AfkActivityListener implements Listener {

  private final ConfigHandle<AfkConfig> config;
  private final AfkService service;
  private final AfkTransitions transitions;
  private final ConcurrentHashMap<UUID, Long> lastMoveRecord = new ConcurrentHashMap<>();

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

    var player = event.getPlayer();
    var id = player.getUniqueId();
    var now = System.currentTimeMillis();
    var previous = this.lastMoveRecord.get(id);
    var debounceMillis = this.config.value().moveDebounceMillis();
    if (previous != null && now - previous < debounceMillis) {
      return;
    }
    this.lastMoveRecord.put(id, now);

    record(player, now);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onInteract(@NonNull PlayerInteractEvent event) {
    record(event.getPlayer(), System.currentTimeMillis());
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onChat(@NonNull AsyncChatEvent event) {
    record(event.getPlayer(), System.currentTimeMillis());
  }

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var id = event.getPlayer().getUniqueId();
    this.lastMoveRecord.remove(id);
  }

  private void record(@NonNull Player player, long now) {
    var id = player.getUniqueId();
    var name = player.getName();

    this.service.recordActivity(id, now);
    this.transitions.exit(id, name);
  }
}
