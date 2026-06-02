package com.hanielcota.essentials.modules.fly.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FlyConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = FlyConfig.defaults();

    assertTrue(!snap.enabled().isEmpty());
    assertTrue(!snap.enabledOther().isEmpty());
    assertTrue(!snap.disabled().isEmpty());
    assertTrue(!snap.disabledOther().isEmpty());
    assertTrue(!snap.unsupported().isEmpty());
    assertTrue(!snap.unsupportedOther().isEmpty());
  }

  @Test
  void toggleEnabledReturnsEnabledPair() {
    var snap = FlyConfig.defaults();
    var pair = snap.toggle(true);

    assertNotNull(pair);
    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void toggleDisabledReturnsDisabledPair() {
    var snap = FlyConfig.defaults();
    var pair = snap.toggle(false);

    assertNotNull(pair);
    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void unsupportedGamemodeReturnsPair() {
    var snap = FlyConfig.defaults();
    var pair = snap.unsupportedGamemode();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }
}
