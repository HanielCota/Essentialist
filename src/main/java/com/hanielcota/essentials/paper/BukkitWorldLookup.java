package com.hanielcota.essentials.paper;

import java.util.Optional;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.World;

/** Production {@link WorldLookup} backed by {@code Bukkit.getWorld}. */
public final class BukkitWorldLookup implements WorldLookup {

  @Override
  public Optional<World> world(@NonNull String name) {
    var world = Bukkit.getWorld(name);
    return Optional.ofNullable(world);
  }
}
