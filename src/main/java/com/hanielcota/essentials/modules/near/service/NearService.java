package com.hanielcota.essentials.modules.near.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class NearService {

  /**
   * Default factory: target is always visible. Use the constructor to inject a vanish-aware one.
   */
  public static NearService allVisible() {
    BiPredicate<Player, Player> alwaysVisible = (viewer, target) -> true;
    return new NearService(alwaysVisible);
  }

  /**
   * Filter applied to every candidate before the distance check. The first argument is the {@code
   * /near} caller, the second is the candidate — used so a viewer with {@code
   * essentials.vanish.see} can still see vanished players in results.
   */
  private final BiPredicate<Player, Player> visibilityFilter;

  /**
   * Returns the players within {@code radius} blocks of {@code center} (same world only), ordered
   * nearest first. The center player is never included; entries failing {@link #visibilityFilter}
   * are skipped.
   */
  public List<Nearby> findNearby(@NonNull Player center, int radius) {
    var origin = center.getLocation();
    var maxDistanceSquared = (double) radius * radius;
    var world = center.getWorld();
    var candidates = world.getPlayers();

    var result = collectMatches(center, origin, maxDistanceSquared, candidates);

    var byDistance = Comparator.comparingInt(Nearby::distance);
    result.sort(byDistance);
    return result;
  }

  private List<Nearby> collectMatches(
      @NonNull Player center,
      @NonNull Location origin,
      double maxDistanceSquared,
      @NonNull Collection<? extends Player> candidates) {

    var matches = new ArrayList<Nearby>();
    for (var other : candidates) {
      if (other.equals(center)) {
        continue;
      }
      if (!this.visibilityFilter.test(center, other)) {
        continue;
      }

      var otherLocation = other.getLocation();
      var distanceSquared = origin.distanceSquared(otherLocation);
      if (distanceSquared > maxDistanceSquared) {
        continue;
      }

      var distance = (int) Math.round(Math.sqrt(distanceSquared));
      var nearby = new Nearby(other, distance);
      matches.add(nearby);
    }
    return matches;
  }

  /** A nearby player and the rounded block distance from the search center. */
  public record Nearby(Player player, int distance) {
    public Nearby {}
  }
}
