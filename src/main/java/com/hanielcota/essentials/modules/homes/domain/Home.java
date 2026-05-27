package com.hanielcota.essentials.modules.homes.domain;

import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
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
    long createdAt,
    boolean pinned) {

  public static Home of(
      @NonNull UUID owner,
      @NonNull String name,
      @NonNull Location location,
      @NonNull Material material) {
    var world = location.getWorld();
    var worldName = world.getName();

    var x = location.getX();
    var y = location.getY();
    var z = location.getZ();
    var yaw = location.getYaw();
    var pitch = location.getPitch();

    var createdAt = System.currentTimeMillis();

    return new Home(owner, name, worldName, x, y, z, yaw, pitch, material, createdAt, false);
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
        this.createdAt,
        this.pinned);
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
        this.createdAt,
        this.pinned);
  }

  public Home withPinned(boolean newPinned) {
    return new Home(
        this.owner,
        this.name,
        this.world,
        this.x,
        this.y,
        this.z,
        this.yaw,
        this.pitch,
        this.material,
        this.createdAt,
        newPinned);
  }

  public Optional<Location> resolve() {
    var resolvedWorld = Bukkit.getWorld(this.world);
    if (resolvedWorld == null) {
      return Optional.empty();
    }

    var location = new Location(resolvedWorld, this.x, this.y, this.z, this.yaw, this.pitch);

    return Optional.of(location);
  }
}
