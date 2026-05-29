package com.hanielcota.essentials.modules.kit.listener;

import com.hanielcota.essentials.modules.kit.repository.CachedKitUsageRepository;
import com.hanielcota.essentials.shared.Log;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/** Loads kit usage during the async login handshake and evicts it on quit. */
@RequiredArgsConstructor
public final class KitUsageCacheListener implements Listener {

  private static final Log LOG = Log.of(KitUsageCacheListener.class);

  private final CachedKitUsageRepository repository;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPreLogin(@NonNull AsyncPlayerPreLoginEvent event) {
    if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
      return;
    }

    var uuid = event.getUniqueId();

    try {
      this.repository.loadFor(uuid);
    } catch (RuntimeException e) {
      LOG.warn(e, "Failed to preload kit usage for {}; cache stays empty until rejoin", uuid);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var uuid = event.getPlayer().getUniqueId();

    this.repository.evictFor(uuid);
  }
}
