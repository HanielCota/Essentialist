package com.hanielcota.essentials.modules.warps.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.service.WarpFilterPreferences;
import com.hanielcota.essentials.modules.warps.service.WarpOccupancy;
import com.hanielcota.essentials.modules.warps.service.WarpSelection;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Keeps {@link WarpOccupancy} accurate: drops a player from a warp's count when they walk out of
 * its radius, teleport away, or disconnect. The hot {@link PlayerMoveEvent} short-circuits on the
 * first check for players who are not currently tracked (the overwhelming majority).
 */
@RequiredArgsConstructor
public final class WarpOccupancyListener implements Listener {

  private final WarpOccupancy occupancy;
  private final WarpSelection selection;
  private final WarpFilterPreferences filters;
  private final ConfigHandle<WarpsConfig> config;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var playerId = event.getPlayer().getUniqueId();
    this.occupancy.leave(playerId);
    this.selection.clear(playerId);
    this.filters.clear(playerId);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMove(@NonNull PlayerMoveEvent event) {
    var playerId = event.getPlayer().getUniqueId();
    if (!this.occupancy.isTracked(playerId)) {
      return;
    }

    var to = event.getTo();
    if (!blockChanged(event.getFrom(), to)) {
      return;
    }

    leaveIfOutside(playerId, to);
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTeleport(@NonNull PlayerTeleportEvent event) {
    var playerId = event.getPlayer().getUniqueId();
    if (!this.occupancy.isTracked(playerId)) {
      return;
    }

    leaveIfOutside(playerId, event.getTo());
  }

  private void leaveIfOutside(@NonNull UUID playerId, Location location) {
    if (location == null) {
      return;
    }

    var world = location.getWorld();
    if (world == null) {
      return;
    }

    var radius = this.config.value().occupancyRadiusBlocks();
    var outside =
        this.occupancy.isOutsideAnchor(
            playerId, world.getName(), location.getX(), location.getY(), location.getZ(), radius);

    if (outside) {
      this.occupancy.leave(playerId);
    }
  }

  private static boolean blockChanged(@NonNull Location from, Location to) {
    if (to == null) {
      return false;
    }

    return from.getBlockX() != to.getBlockX()
        || from.getBlockY() != to.getBlockY()
        || from.getBlockZ() != to.getBlockZ();
  }
}
