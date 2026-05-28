package com.hanielcota.essentials.modules.warps.domain;

import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Location;

/**
 * One persisted server warp.
 *
 * <p>Stores the world name (not a live {@code World}) so the record stays valid across reloads.
 * Materialization back into a {@link Location} lives in {@code WarpResolver} — the domain record is
 * a pure value carrier.
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
    @NonNull UUID createdBy) {

  /** Captures a Bukkit {@link Location} as a fresh warp. */
  public static Warp of(@NonNull String name, @NonNull Location location, @NonNull UUID createdBy) {
    var worldInstance = location.getWorld();
    var worldName = worldInstance.getName();

    var x = location.getX();
    var y = location.getY();
    var z = location.getZ();

    var yaw = location.getYaw();
    var pitch = location.getPitch();
    var currentTime = System.currentTimeMillis();

    return new Warp(name, worldName, x, y, z, yaw, pitch, currentTime, createdBy);
  }
}
