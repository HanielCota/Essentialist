package com.hanielcota.essentials.modules.homes.listener;

import com.hanielcota.essentials.modules.homes.repository.CachedHomeRepository;
import com.hanielcota.essentials.util.Log;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Per-player cache lifecycle: load homes from SQL during the async login handshake (cache is ready
 * before the player can run any command), evict on quit to free memory.
 *
 * <p>Hooked at {@link AsyncPlayerPreLoginEvent} (not PlayerJoin) because that event is already on
 * an off-tick thread; blocking on SQL there does not steal main-thread budget, and the load
 * completes before the player object exists and can issue commands.
 */
@RequiredArgsConstructor
public final class HomesCacheListener implements Listener {

  private static final Log LOG = Log.of(HomesCacheListener.class);

  private final CachedHomeRepository repository;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPreLogin(@NonNull AsyncPlayerPreLoginEvent event) {
    var result = event.getLoginResult();

    if (result != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
      return;
    }

    var uuid = event.getUniqueId();

    try {
      this.repository.loadFor(uuid);
    } catch (RuntimeException e) {
      LOG.warn(e, "Failed to preload homes for {}; cache stays empty until rejoin", uuid);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var uuid = player.getUniqueId();

    this.repository.evictFor(uuid);
  }
}
