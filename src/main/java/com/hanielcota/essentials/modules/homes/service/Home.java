package com.hanielcota.essentials.modules.homes.service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * One persisted home of a player.
 *
 * <p>Stores the world name (not a live {@code World}) so the record stays valid across reloads and
 * world unloads. {@link #resolve()} returns empty when the world is no longer available.
 */
public record Home(
    UUID owner,
    String name,
    String world,
    double x,
    double y,
    double z,
    float yaw,
    float pitch,
    long createdAt) {

  public Home {
    Objects.requireNonNull(owner, "owner");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(world, "world");
  }

  /** Captures a Bukkit {@link Location} as a fresh home record. */
  public static Home of(UUID owner, String name, Location location) {
    Objects.requireNonNull(owner, "owner");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(location, "location");
    var world = location.getWorld();
    Objects.requireNonNull(world, "location world");
    return new Home(
        owner,
        name,
        world.getName(),
        location.getX(),
        location.getY(),
        location.getZ(),
        location.getYaw(),
        location.getPitch(),
        System.currentTimeMillis());
  }

  /** Materialises the home back into a Bukkit location, or empty when the world is unloaded. */
  public Optional<Location> resolve() {
    var w = Bukkit.getWorld(world);
    if (w == null) {
      return Optional.empty();
    }
    return Optional.of(new Location(w, x, y, z, yaw, pitch));
  }
}
