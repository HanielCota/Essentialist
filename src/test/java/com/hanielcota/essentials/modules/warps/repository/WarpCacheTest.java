package com.hanielcota.essentials.modules.warps.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class WarpCacheTest {

  private static Warp w(String name) {
    return new Warp(name, "world", 0.0, 64.0, 0.0, 0.0f, 0.0f, 1L, UUID.randomUUID());
  }

  @Test
  void findIsCaseInsensitive() {
    var cache = new WarpCache();
    cache.put(w("Spawn"));

    assertTrue(cache.find("spawn").isPresent());
    assertTrue(cache.find("SPAWN").isPresent());
    assertTrue(cache.find("Spawn").isPresent());
  }

  @Test
  void findReturnsEmptyForUnknown() {
    var cache = new WarpCache();

    assertFalse(cache.find("none").isPresent());
  }

  @Test
  void listReturnsSortedSnapshot() {
    var cache = new WarpCache();
    cache.put(w("Beta"));
    cache.put(w("Alpha"));
    cache.put(w("Gamma"));

    var list = cache.list();

    assertEquals(3, list.size());
    assertEquals("Alpha", list.get(0).name());
    assertEquals("Beta", list.get(1).name());
    assertEquals("Gamma", list.get(2).name());
  }

  @Test
  void removeReturnsPreviousValue() {
    var cache = new WarpCache();
    cache.put(w("Spawn"));

    var removed = cache.remove("spawn");

    assertTrue(removed.isPresent());
    assertEquals("Spawn", removed.get().name());
    assertFalse(cache.find("Spawn").isPresent());
  }

  @Test
  void removeReturnsEmptyForUnknown() {
    var cache = new WarpCache();

    assertFalse(cache.remove("none").isPresent());
  }

  @Test
  void loadAllReplacesExistingEntries() {
    var cache = new WarpCache();
    cache.put(w("Old"));

    cache.loadAll(List.of(w("New")));

    assertFalse(cache.find("Old").isPresent());
    assertTrue(cache.find("New").isPresent());
  }
}
