package com.hanielcota.essentials.modules.warps.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class WarpFiltersTest {

  private static final Warp A = warp("a");
  private static final Warp B = warp("b");
  private static final Warp C = warp("c");

  private static Warp warp(String name) {
    return new Warp(name, "world", 0, 64, 0, 0, 0, 0L, UUID.randomUUID(), Material.ENDER_PEARL);
  }

  private static List<String> names(List<Warp> warps) {
    return warps.stream().map(Warp::name).toList();
  }

  @Test
  void mostPlayersSortsDescending() {
    var data = new FakeData(Map.of("a", 1, "b", 2), Map.of(), Set.of(), Set.of());

    var result = WarpFilters.apply(List.of(A, B, C), WarpFilter.MOST_PLAYERS, data);

    assertEquals(List.of("b", "a", "c"), names(result));
  }

  @Test
  void leastPlayersSortsAscending() {
    var data = new FakeData(Map.of("a", 1, "b", 2), Map.of(), Set.of(), Set.of());

    var result = WarpFilters.apply(List.of(A, B, C), WarpFilter.LEAST_PLAYERS, data);

    assertEquals(List.of("c", "a", "b"), names(result));
  }

  @Test
  void mostLikedSortsDescending() {
    var data = new FakeData(Map.of(), Map.of("a", 1, "b", 2), Set.of(), Set.of());

    var result = WarpFilters.apply(List.of(A, B, C), WarpFilter.MOST_LIKED, data);

    assertEquals(List.of("b", "a", "c"), names(result));
  }

  @Test
  void favoritesKeepsOnlyFavorited() {
    var data = new FakeData(Map.of(), Map.of(), Set.of("b"), Set.of());

    var result = WarpFilters.apply(List.of(A, B, C), WarpFilter.FAVORITES, data);

    assertEquals(List.of("b"), names(result));
  }

  @Test
  void pvpKeepsOnlyFlagged() {
    var data = new FakeData(Map.of(), Map.of(), Set.of(), Set.of("b"));

    var result = WarpFilters.apply(List.of(A, B, C), WarpFilter.PVP, data);

    assertEquals(List.of("b"), names(result));
  }

  @Test
  void defaultReturnsTheListUnchanged() {
    var data = new FakeData(Map.of(), Map.of(), Set.of(), Set.of());

    var result = WarpFilters.apply(List.of(A, B, C), WarpFilter.DEFAULT, data);

    assertEquals(List.of("a", "b", "c"), names(result));
  }

  private record FakeData(
      Map<String, Integer> playerCounts,
      Map<String, Integer> likeCounts,
      Set<String> favoriteWarps,
      Set<String> pvpWarps)
      implements WarpFilterData {

    @Override
    public int players(String warpName) {
      return playerCounts.getOrDefault(warpName, 0);
    }

    @Override
    public int likes(String warpName) {
      return likeCounts.getOrDefault(warpName, 0);
    }

    @Override
    public boolean favorite(String warpName) {
      return favoriteWarps.contains(warpName);
    }

    @Override
    public boolean pvp(String warpName) {
      return pvpWarps.contains(warpName);
    }
  }
}
