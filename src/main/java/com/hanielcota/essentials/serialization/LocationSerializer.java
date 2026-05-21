package com.hanielcota.essentials.serialization;

import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationSerializer implements Serializer<Location> {

  @Override
  public Class<Location> type() {
    return Location.class;
  }

  @Override
  public String serialize(Location value) {
    Objects.requireNonNull(value, "value");
    World world = value.getWorld();
    if (world == null) {
      throw new IllegalArgumentException("Location has no world");
    }
    return String.join(
        ";",
        world.getName(),
        Double.toString(value.getX()),
        Double.toString(value.getY()),
        Double.toString(value.getZ()),
        Float.toString(value.getYaw()),
        Float.toString(value.getPitch()));
  }

  @Override
  public Location deserialize(String raw) {
    Objects.requireNonNull(raw, "raw");
    String[] parts = raw.split(";");
    if (parts.length != 6) {
      throw new IllegalArgumentException("Invalid location format: " + raw);
    }
    World world = Bukkit.getWorld(parts[0]);
    if (world == null) {
      throw new IllegalStateException("Unknown world: " + parts[0]);
    }
    return new Location(
        world,
        Double.parseDouble(parts[1]),
        Double.parseDouble(parts[2]),
        Double.parseDouble(parts[3]),
        Float.parseFloat(parts[4]),
        Float.parseFloat(parts[5]));
  }
}
