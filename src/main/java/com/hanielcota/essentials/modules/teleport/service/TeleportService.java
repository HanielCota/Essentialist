package com.hanielcota.essentials.modules.teleport.service;

import com.hanielcota.essentials.api.TeleportsApi;
import com.hanielcota.essentials.modules.teleport.domain.TeleportOutcome;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class TeleportService implements TeleportsApi {

  private static CompletableFuture<TeleportOutcome> dispatch(
      @NonNull Player subject, @NonNull Location destination) {
    var teleport = subject.teleportAsync(destination);

    return teleport.thenApply(TeleportService::translate);
  }

  private static TeleportOutcome translate(Boolean success) {
    if (Boolean.TRUE.equals(success)) {
      return TeleportOutcome.SUCCESS;
    }

    return TeleportOutcome.FAILED;
  }

  private static CompletableFuture<TeleportOutcome> rejected(@NonNull TeleportOutcome outcome) {
    return CompletableFuture.completedFuture(outcome);
  }

  private static boolean sameId(@NonNull Player a, @NonNull Player b) {
    var aId = a.getUniqueId();
    var bId = b.getUniqueId();

    return aId.equals(bId);
  }

  public CompletableFuture<TeleportOutcome> toPlayer(
      @NonNull Player sender, @NonNull Player target) {
    if (sameId(sender, target)) {
      return rejected(TeleportOutcome.SELF_TARGET);
    }

    var destination = target.getLocation();

    return dispatch(sender, destination);
  }

  public CompletableFuture<TeleportOutcome> movePlayer(@NonNull Player from, @NonNull Player to) {
    if (sameId(from, to)) {
      return rejected(TeleportOutcome.SELF_TARGET);
    }

    var destination = to.getLocation();

    return dispatch(from, destination);
  }

  public CompletableFuture<TeleportOutcome> toPosition(
      @NonNull Player sender, double x, double y, double z) {
    var world = sender.getWorld();
    var currentLocation = sender.getLocation();
    var currentYaw = currentLocation.getYaw();
    var currentPitch = currentLocation.getPitch();
    var destination = new Location(world, x, y, z, currentYaw, currentPitch);

    var invalidReason = validatePosition(destination, world);
    if (invalidReason != null) {
      return rejected(invalidReason);
    }

    return dispatch(sender, destination);
  }

  public CompletableFuture<TeleportOutcome> bringHere(
      @NonNull Player viewer, @NonNull Player target) {
    if (sameId(viewer, target)) {
      return rejected(TeleportOutcome.SELF_TARGET);
    }

    var destination = viewer.getLocation();

    return dispatch(target, destination);
  }

  private static TeleportOutcome validatePosition(
      @NonNull Location destination, @NonNull World world) {
    var minHeight = world.getMinHeight();
    var maxHeight = world.getMaxHeight();
    var worldBorder = world.getWorldBorder();
    var insideBorder = worldBorder.isInside(destination);

    if (destination.getY() < minHeight || destination.getY() >= maxHeight || !insideBorder) {
      return TeleportOutcome.INVALID_POSITION;
    }

    return null;
  }
}
