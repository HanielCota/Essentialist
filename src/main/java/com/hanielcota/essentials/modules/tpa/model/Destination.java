package com.hanielcota.essentials.modules.tpa.model;

import java.util.Objects;
import org.bukkit.Location;

/** Immutable snapshot of where an accepted teleport landed. */
public record Destination(String world, double x, double y, double z) {

  public Destination {
    Objects.requireNonNull(world, "world");
  }

  /** Captures a Bukkit {@link Location} as an immutable destination. */
  public static Destination of(Location location) {
    Objects.requireNonNull(location, "location");
    var world = location.getWorld();
    Objects.requireNonNull(world, "location world");
    return new Destination(world.getName(), location.getX(), location.getY(), location.getZ());
  }
}
