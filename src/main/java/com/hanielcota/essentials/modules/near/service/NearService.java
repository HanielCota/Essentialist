package com.hanielcota.essentials.modules.near.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class NearService {

  /** Default factory: always-visible filter. Use the constructor to inject a vanish-aware one. */
  public static NearService allVisible() {
    return new NearService(player -> true);
  }

  /** Filter applied to every candidate before distance check — used to hide vanished players. */
  private final Predicate<Player> visibilityFilter;

  /**
   * Returns the players within {@code radius} blocks of {@code center} (same world only), ordered
   * nearest first. The center player is never included; entries failing {@link #visibilityFilter}
   * are skipped.
   */
  public List<Nearby> findNearby(@NonNull Player center, int radius) {

    var origin = center.getLocation();
    double maxDistanceSquared = (double) radius * radius;
    var result = new ArrayList<Nearby>();

    for (Player other : center.getWorld().getPlayers()) {
      if (other.equals(center)) {
        continue;
      }
      if (!this.visibilityFilter.test(other)) {
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
