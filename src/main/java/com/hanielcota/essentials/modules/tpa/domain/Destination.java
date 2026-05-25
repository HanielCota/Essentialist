package com.hanielcota.essentials.modules.tpa.domain;

import lombok.NonNull;
import org.bukkit.Location;

/** Immutable snapshot of where an accepted teleport landed. */
public record Destination(String world, double x, double y, double z) {

  /** Captures a Bukkit {@link Location} as an immutable destination. */
  public static Destination of(@NonNull Location location) {
    var world = location.getWorld();
    return new Destination(world.getName(), location.getX(), location.getY(), location.getZ());
  }
}
