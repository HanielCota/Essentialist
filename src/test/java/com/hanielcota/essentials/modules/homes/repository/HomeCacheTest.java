package com.hanielcota.essentials.modules.homes.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.homes.domain.Home;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class HomeCacheTest {

  private static Home home(UUID owner, String name) {
    return new Home(owner, name, "world", 1, 2, 3, 0, 0, Material.RED_BED, 1);
  }

  @Test
  void storesAndFindsHomesCaseInsensitively() {
    var owner = UUID.randomUUID();
    var cache = new HomeCache(List.of(home(owner, "base")));

    assertTrue(cache.find(owner, "BASE").isPresent());
    assertEquals(1, cache.count(owner));
    assertEquals(List.of("base"), cache.list(owner).stream().map(Home::name).toList());
  }

  @Test
  void renameDeleteAndMaterialUpdateAreAtomicForOnePlayerBucket() {
    var owner = UUID.randomUUID();
    var cache = new HomeCache(List.of(home(owner, "base")));

    assertTrue(cache.rename(owner, "BASE", "main").isPresent());
    assertFalse(cache.find(owner, "base").isPresent());
    assertEquals(
        Material.COMPASS,
        cache.updateMaterial(owner, "MAIN", Material.COMPASS).orElseThrow().material());
    assertTrue(cache.delete(owner, "main").isPresent());
    assertEquals(0, cache.count(owner));
  }
}
