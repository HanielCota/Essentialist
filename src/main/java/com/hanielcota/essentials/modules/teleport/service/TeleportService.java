package com.hanielcota.essentials.modules.teleport.service;

import java.util.concurrent.CompletableFuture;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Owns the teleport flow for {@code /tp}, {@code /tphere} and friends. Validates pre-flight
 * conditions (same-target / world bounds / world border) and delegates the actual move to Paper's
 * async teleport API. Returns a {@link CompletableFuture} of {@link TeleportOutcome} which the
 * caller's notifier consumes.
 *
 * <p>Stateless — every call is independent. Pre-flight rejections short-circuit by completing the
 * future immediately so callers always observe a single completion signal.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TeleportService {

  public static CompletableFuture<TeleportOutcome> toPlayer(
      @NonNull Player sender, @NonNull Player target) {
    if (sameId(sender, target)) {
      return rejected(new TeleportOutcome.SelfTarget());
    }

    var destination = target.getLocation();

    return dispatch(sender, destination);
  }

  public static CompletableFuture<TeleportOutcome> movePlayer(
      @NonNull Player from, @NonNull Player to) {
    if (sameId(from, to)) {
      return rejected(new TeleportOutcome.SelfTarget());
    }

    var destination = to.getLocation();

    return dispatch(from, destination);
  }

  public static CompletableFuture<TeleportOutcome> toPosition(
      @NonNull Player sender, double x, double y, double z) {
    var world = sender.getWorld();
    var currentLocation = sender.getLocation();
    var currentYaw = currentLocation.getYaw();
    var currentPitch = currentLocation.getPitch();
    var destination = new Location(world, x, y, z, currentYaw, currentPitch);

    var minHeight = world.getMinHeight();
    var maxHeight = world.getMaxHeight();
    var worldBorder = world.getWorldBorder();
    var insideBorder = worldBorder.isInside(destination);
    if (y < minHeight || y >= maxHeight || !insideBorder) {
      return rejected(new TeleportOutcome.InvalidPosition());
    }

    return dispatch(sender, destination);
  }

  public static CompletableFuture<TeleportOutcome> bringHere(
      @NonNull Player viewer, @NonNull Player target) {
    if (sameId(viewer, target)) {
      return rejected(new TeleportOutcome.SelfTarget());
    }

    var destination = viewer.getLocation();

    return dispatch(target, destination);
  }

  private static CompletableFuture<TeleportOutcome> dispatch(
      @NonNull Player subject, @NonNull Location destination) {
    var teleport = subject.teleportAsync(destination);

    return teleport.thenApply(TeleportService::translate);
  }

  private static TeleportOutcome translate(Boolean success) {
    if (Boolean.TRUE.equals(success)) {
      return new TeleportOutcome.Success();
    }

    return new TeleportOutcome.Failed();
  }

  private static CompletableFuture<TeleportOutcome> rejected(@NonNull TeleportOutcome outcome) {
    return CompletableFuture.completedFuture(outcome);
  }

  private static boolean sameId(@NonNull Player a, @NonNull Player b) {
    var aId = a.getUniqueId();
    var bId = b.getUniqueId();

    return aId.equals(bId);
  }
}
