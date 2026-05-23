package com.hanielcota.essentials.modules.homes.domain;

import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * One persisted home of a player.
 *
 * <p>Stores the world name (not a live {@code World}) so the record stays valid across reloads and
 * world unloads. {@link #resolve()} returns null when the world is no longer available.
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

  public static Home of(
      @NonNull UUID owner,
      @NonNull String name,
      @NonNull Location location,
      @NonNull Material material) {
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

  public Home withName(@NonNull String newName) {
    return new Home(
        this.owner,
        newName,
        this.world,
        this.x,
        this.y,
        this.z,
        this.yaw,
        this.pitch,
        this.material,
        this.createdAt);
  }

  public Home withMaterial(@NonNull Material newMaterial) {
    return new Home(
        this.owner,
        this.name,
        this.world,
        this.x,
        this.y,
        this.z,
        this.yaw,
        this.pitch,
        newMaterial,
        this.createdAt);
  }

  public Location resolve() {
    var w = Bukkit.getWorld(this.world);
    return w != null ? new Location(w, this.x, this.y, this.z, this.yaw, this.pitch) : null;
  }
}
