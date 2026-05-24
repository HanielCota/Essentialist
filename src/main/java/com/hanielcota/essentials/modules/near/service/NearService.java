package com.hanielcota.essentials.modules.near.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class NearService {

  /**
   * Returns the players within {@code radius} blocks of {@code center} (same world only), ordered
   * nearest first. The center player is never included.
   */
  public List<Nearby> findNearby(@NonNull Player center, int radius) {

    var origin = center.getLocation();
    double maxDistanceSquared = (double) radius * radius;
    var result = new ArrayList<Nearby>();

    for (Player other : center.getWorld().getPlayers()) {
      if (other.equals(center)) {
        continue;
      }
      double distanceSquared = origin.distanceSquared(other.getLocation());

      if (distanceSquared <= maxDistanceSquared) {
        var distance = (int) Math.round(Math.sqrt(distanceSquared));
        var nearby = new Nearby(other, distance);

        result.add(nearby);
      }
    }

    result.sort(Comparator.comparingInt(Nearby::distance));
    return result;
  }

  /** A nearby player and the rounded block distance from the search center. */
  public record Nearby(Player player, int distance) {
    public Nearby {}
  }
}
