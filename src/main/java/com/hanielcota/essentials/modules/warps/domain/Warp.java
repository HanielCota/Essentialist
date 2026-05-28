package com.hanielcota.essentials.modules.warps.domain;

import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * One persisted server warp.
 *
 * <p>Stores the world name (not a live {@code World}) so the record stays valid across reloads.
 * Materialization back into a {@link Location} lives in {@code WarpResolver} — the domain record is
 * a pure value carrier. {@code icon} is the menu material; a config override may take precedence at
 * render time (see {@code WarpsConfig#iconFor}).
 */
public record Warp(
    @NonNull String name,
    @NonNull String world,
    double x,
    double y,
    double z,
    float yaw,
    float pitch,
    long createdAt,
    @NonNull UUID createdBy,
    @NonNull Material icon) {

  /** Captures a Bukkit {@link Location} as a fresh warp with the given menu icon. */
  public static Warp of(
      @NonNull String name,
      @NonNull Location location,
      @NonNull UUID createdBy,
      @NonNull Material icon) {
    var worldInstance = location.getWorld();
    var worldName = worldInstance.getName();

    var x = location.getX();
    var y = location.getY();
    var z = location.getZ();

    var yaw = location.getYaw();
    var pitch = location.getPitch();
    var currentTime = System.currentTimeMillis();

    return new Warp(name, worldName, x, y, z, yaw, pitch, currentTime, createdBy, icon);
  }

  /**
   * A copy of this warp moved to a new location with a new icon, preserving the original {@code
   * name}, {@code createdAt} and {@code createdBy}. Used when {@code /setwarp} overwrites an
   * existing warp so the original authorship and creation time survive the relocation.
   */
  public Warp movedTo(@NonNull Location location, @NonNull Material newIcon) {
    var worldInstance = location.getWorld();
    var worldName = worldInstance.getName();

    var newX = location.getX();
    var newY = location.getY();
    var newZ = location.getZ();

    var newYaw = location.getYaw();
    var newPitch = location.getPitch();

    return new Warp(
        this.name,
        worldName,
        newX,
        newY,
        newZ,
        newYaw,
        newPitch,
        this.createdAt,
        this.createdBy,
        newIcon);
  }
}
