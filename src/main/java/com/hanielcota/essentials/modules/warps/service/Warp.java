package com.hanielcota.essentials.modules.warps.service;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * One persisted server warp.
 *
 * <p>Stores the world name (not a live {@code World}) so the record stays valid across reloads.
 * {@link #resolve()} returns empty when the world is no longer available.
 */
public record Warp(
    String name,
    String world,
    double x,
    double y,
    double z,
    float yaw,
    float pitch,
    long createdAt,
    UUID createdBy) {

  /** Captures a Bukkit {@link Location} as a fresh warp. */
  public static Warp of(String name, Location location, UUID createdBy) {
    var world = location.getWorld();
    return new Warp(
        name,
        world.getName(),
        location.getX(),
        location.getY(),
        location.getZ(),
        location.getYaw(),
        location.getPitch(),
        System.currentTimeMillis(),
        createdBy);
  }

  /** Materialises the warp back into a Bukkit location, or empty when the world is unloaded. */
  public Optional<Location> resolve() {
    var w = Bukkit.getWorld(world);
    if (w == null) {
      return Optional.empty();
    }
    return Optional.of(new Location(w, x, y, z, yaw, pitch));
  }
}
