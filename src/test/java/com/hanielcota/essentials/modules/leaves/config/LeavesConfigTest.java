package com.hanielcota.essentials.modules.leaves.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.leaves.config.LeavesConfig.WorldMode;
import java.util.List;
import org.junit.jupiter.api.Test;

class LeavesConfigTest {

  @Test
  void whitelistAppliesOnlyToListedWorlds() {
    var snap = new LeavesConfig(true, WorldMode.WHITELIST, List.of("world"));

    assertTrue(snap.appliesTo("world"));
    assertFalse(snap.appliesTo("world_nether"));
  }

  @Test
  void blacklistAppliesEverywhereExceptListedWorlds() {
    var snap = new LeavesConfig(true, WorldMode.BLACKLIST, List.of("world"));

    assertFalse(snap.appliesTo("world"));
    assertTrue(snap.appliesTo("world_nether"));
  }

  @Test
  void defaultsProtectEveryWorld() {
    var snap = LeavesConfig.defaults();

    assertTrue(snap.enabled());
    assertTrue(snap.appliesTo("world"));
    assertTrue(snap.appliesTo("world_nether"));
    assertTrue(snap.appliesTo("anything"));
  }
}
