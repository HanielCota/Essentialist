package com.hanielcota.essentials.modules.spawn.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SpawnConfigTest {

  @Test
  void defaultsHaveTeleportDelaySeconds() {
    var snap = SpawnConfig.defaults();

    assertTrue(snap.teleportDelaySeconds() >= 0);
  }

  @Test
  void teleportDelayReturnsNonNegativeDuration() {
    var snap = SpawnConfig.defaults();
    var delay = snap.teleportDelay();

    assertTrue(!delay.isNegative());
  }

  @Test
  void messagesArePresent() {
    var snap = SpawnConfig.defaults();

    assertNotNull(snap.messages());
  }
}
