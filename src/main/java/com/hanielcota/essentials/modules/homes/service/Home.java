package com.hanielcota.essentials.modules.homes.service;

import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

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
    Material material,
    long createdAt) {

  public static Home of(UUID owner, String name, Location location, Material material) {
    var world = location.getWorld();
    return new Home(
        owner,
        name,
        world.getName(),
        location.getX(),
        location.getY(),
        location.getZ(),
        location.getYaw(),
        location.getPitch(),
        material,
        System.currentTimeMillis());
  }

  public Home withName(String newName) {
    return new Home(owner, newName, world, x, y, z, yaw, pitch, material, createdAt);
  }

  public Home withMaterial(Material newMaterial) {
    return new Home(owner, name, world, x, y, z, yaw, pitch, newMaterial, createdAt);
  }

  public Optional<Location> resolve() {
    var w = Bukkit.getWorld(world);
    if (w == null) {
      return Optional.empty();
    }
    return Optional.of(new Location(w, x, y, z, yaw, pitch));
  }
}
