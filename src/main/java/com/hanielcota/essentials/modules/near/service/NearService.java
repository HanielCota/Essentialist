package com.hanielcota.essentials.modules.near.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiPredicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class NearService {

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

    // Pre-size to the upper bound (every candidate is in range) to avoid the default capacity-10
    // ArrayList grow-by-50% cycle on dense worlds. Over-allocation is cheap; reallocs on every
    // /near are not.
    var matches = new ArrayList<Nearby>(candidates.size());
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
      var nearby = new Nearby(other.getUniqueId(), other.getName(), distance);
      matches.add(nearby);
    }
    return matches;
  }

  /**
   * Snapshot of a nearby player. Carries {@link UUID} + name rather than {@link Player} so the
   * record stays valid past the originating tick — Bukkit's {@code Player} reference goes stale on
   * disconnect.
   */
  public record Nearby(UUID id, String name, int distance) {
    public Nearby {}
  }
}
