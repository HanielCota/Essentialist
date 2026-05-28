package com.hanielcota.essentials.modules.warps.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;

/**
 * Tracks how many players are currently "at" each warp, maintained incrementally so the menu never
 * scans the whole online player list. A player is recorded on warp arrival ({@link #enter}) and
 * dropped when they leave the warp's radius, teleport away, or disconnect ({@link #leave}). All
 * access is from the main thread, so plain maps suffice. Deliberately Bukkit-free (primitives only)
 * so it is unit-testable without a server.
 */
public final class WarpOccupancy {

  private record Anchor(String warp, String world, double x, double y, double z) {}

  private final Map<String, Set<UUID>> playersByWarp = new HashMap<>();
  private final Map<UUID, Anchor> anchorByPlayer = new HashMap<>();

  public void enter(
      @NonNull UUID playerId,
      @NonNull String warpName,
      @NonNull String world,
      double x,
      double y,
      double z) {
    leave(playerId);

    var anchor = new Anchor(warpName, world, x, y, z);
    anchorByPlayer.put(playerId, anchor);
    playersByWarp.computeIfAbsent(warpName, key -> new HashSet<>()).add(playerId);
  }

  public void leave(@NonNull UUID playerId) {
    var anchor = anchorByPlayer.remove(playerId);
    if (anchor == null) {
      return;
    }

    var occupants = playersByWarp.get(anchor.warp());
    if (occupants == null) {
      return;
    }

    occupants.remove(playerId);
    if (occupants.isEmpty()) {
      playersByWarp.remove(anchor.warp());
    }
  }

  public int count(@NonNull String warpName) {
    var occupants = playersByWarp.get(warpName);
    return occupants == null ? 0 : occupants.size();
  }

  /** Snapshot of the players currently at the warp. */
  public @NonNull Set<UUID> occupants(@NonNull String warpName) {
    var occupants = playersByWarp.get(warpName);
    return occupants == null ? Set.of() : Set.copyOf(occupants);
  }

  public boolean isTracked(@NonNull UUID playerId) {
    return anchorByPlayer.containsKey(playerId);
  }

  /** Whether the player has wandered out of the warp they entered (or changed worlds). */
  public boolean isOutsideAnchor(
      @NonNull UUID playerId, @NonNull String world, double x, double y, double z, int radius) {
    var anchor = anchorByPlayer.get(playerId);
    if (anchor == null) {
      return false;
    }

    if (!anchor.world().equals(world)) {
      return true;
    }

    var dx = anchor.x() - x;
    var dy = anchor.y() - y;
    var dz = anchor.z() - z;
    var distanceSq = dx * dx + dy * dy + dz * dz;
    var radiusSq = (double) radius * radius;

    return distanceSq > radiusSq;
  }
}
