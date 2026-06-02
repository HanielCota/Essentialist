package com.hanielcota.essentials.modules.teleport.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class TeleportServiceTest {

  private static Player player(UUID id, World world, Location location) {
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getUniqueId" -> id;
                case "getWorld" -> world;
                case "getLocation" -> location;
                case "teleportAsync" -> CompletableFuture.completedFuture(true);
                case "getEffectivePermissions" -> Set.of();
                case "toString" -> "TeleportPlayer";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  private static World world(UUID id, int minHeight, int maxHeight, boolean insideBorder) {
    var border =
        (org.bukkit.WorldBorder)
            Proxy.newProxyInstance(
                org.bukkit.WorldBorder.class.getClassLoader(),
                new Class<?>[] {org.bukkit.WorldBorder.class},
                (ignored, method, args) -> {
                  if (method.getName().equals("isInside")) {
                    return insideBorder;
                  }
                  throw new UnsupportedOperationException(method.getName());
                });

    return (World)
        Proxy.newProxyInstance(
            World.class.getClassLoader(),
            new Class<?>[] {World.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getUID" -> id;
                case "getMinHeight" -> minHeight;
                case "getMaxHeight" -> maxHeight;
                case "getWorldBorder" -> border;
                case "toString" -> "World";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  @Test
  void toPlayerRejectsSelfTarget() {
    var service = new TeleportService();
    var id = UUID.randomUUID();
    var w = world(UUID.randomUUID(), -64, 320, true);
    var loc = new Location(w, 0, 64, 0);
    var sender = player(id, w, loc);
    var target = player(id, w, loc);

    var future = service.toPlayer(sender, target);
    var outcome = future.join();

    assertEquals(
        com.hanielcota.essentials.modules.teleport.domain.TeleportOutcome.SELF_TARGET, outcome);
  }

  @Test
  void bringHereRejectsSelfTarget() {
    var service = new TeleportService();
    var id = UUID.randomUUID();
    var w = world(UUID.randomUUID(), -64, 320, true);
    var loc = new Location(w, 0, 64, 0);
    var viewer = player(id, w, loc);
    var target = player(id, w, loc);

    var future = service.bringHere(viewer, target);
    var outcome = future.join();

    assertEquals(
        com.hanielcota.essentials.modules.teleport.domain.TeleportOutcome.SELF_TARGET, outcome);
  }

  @Test
  void toPositionRejectsCoordinatesBelowMinHeight() {
    var service = new TeleportService();
    var id = UUID.randomUUID();
    var w = world(UUID.randomUUID(), -64, 320, true);
    var loc = new Location(w, 0, 64, 0);
    var sender = player(id, w, loc);

    var future = service.toPosition(sender, 0, -100, 0);
    var outcome = future.join();

    assertEquals(
        com.hanielcota.essentials.modules.teleport.domain.TeleportOutcome.INVALID_POSITION,
        outcome);
  }

  @Test
  void toPositionAcceptsValidCoordinates() {
    var service = new TeleportService();
    var id = UUID.randomUUID();
    var w = world(UUID.randomUUID(), -64, 320, true);
    var loc = new Location(w, 0, 64, 0);
    var sender = player(id, w, loc);

    var future = service.toPosition(sender, 10, 70, 10);
    var outcome = future.join();

    assertEquals(
        com.hanielcota.essentials.modules.teleport.domain.TeleportOutcome.SUCCESS, outcome);
  }
}
