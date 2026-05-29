package com.hanielcota.essentials.modules.spawnmob.service;

import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/** Spawns mobs at the player's location, on that player's region thread. */
@RequiredArgsConstructor
public final class SpawnMobService {

  private final Scheduler scheduler;

  public void spawn(@NonNull Player player, @NonNull EntityType type, int amount) {
    var world = player.getWorld();
    var location = player.getLocation();

    this.scheduler.runOnEntity(player, () -> spawnAll(world, location, type, amount));
  }

  private static void spawnAll(
      @NonNull World world, @NonNull Location location, @NonNull EntityType type, int amount) {
    for (var spawned = 0; spawned < amount; spawned++) {
      world.spawnEntity(location, type);
    }
  }
}
