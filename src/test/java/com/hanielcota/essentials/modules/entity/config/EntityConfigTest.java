package com.hanielcota.essentials.modules.entity.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class EntityConfigTest {

  @Test
  void whitelistAppliesOnlyToListedWorlds() {
    var snap =
        new EntityConfig(
            true,
            EntityConfig.WorldMode.WHITELIST,
            List.of("build"),
            "",
            true,
            true,
            true,
            true,
            false,
            List.of(),
            List.of());

    assertTrue(snap.appliesTo("build"));
    assertFalse(snap.appliesTo("world"));
  }

  @Test
  void spawnFiltersMatchListedReasonsAndTypes() {
    var snap =
        new EntityConfig(
            true,
            EntityConfig.WorldMode.BLACKLIST,
            List.of(),
            "",
            false,
            false,
            false,
            false,
            true,
            List.of("NATURAL"),
            List.of("ZOMBIE"));

    assertTrue(snap.isSpawnReasonBlocked("NATURAL"));
    assertFalse(snap.isSpawnReasonBlocked("SPAWNER"));
    assertTrue(snap.isSpawnTypeBlocked("ZOMBIE"));
    assertFalse(snap.isSpawnTypeBlocked("COW"));
  }

  @Test
  void defaultsDisableEveryProtection() {
    var snap = EntityConfig.defaults();

    assertTrue(snap.enabled());
    assertFalse(snap.protectItemFrames());
    assertFalse(snap.preventMobSpawns());
  }
}
