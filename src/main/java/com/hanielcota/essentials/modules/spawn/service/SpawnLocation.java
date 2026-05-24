package com.hanielcota.essentials.modules.spawn.service;

import java.util.Optional;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Immutable snapshot of the configured spawn point.
 *
 * <p>Stores the world name (not the live {@code World}) so the value is safe to keep across reloads
 * and worlds being unloaded. {@link #resolve()} returns empty when the world is no longer present.
 */
public record SpawnLocation(String world, double x, double y, double z, float yaw, float pitch) {

  /** Captures a Bukkit {@link Location} as an immutable spawn point. */
  public static SpawnLocation of(@NonNull Location location) {
    var world = location.getWorld();

    return new SpawnLocation(
        world.getName(),
        location.getX(),
        location.getY(),
        location.getZ(),
        location.getYaw(),
        location.getPitch());
  }

  /** Materializes the spawn back into a Bukkit location, or empty when the world is unloaded. */
  public Optional<Location> resolve() {
    var w = Bukkit.getWorld(this.world);
    if (w == null) {
      return Optional.empty();
    }
    var location = new Location(w, this.x, this.y, this.z, this.yaw, this.pitch);
    return Optional.of(location);
  }
}
