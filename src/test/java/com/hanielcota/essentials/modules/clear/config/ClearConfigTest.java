package com.hanielcota.essentials.modules.clear.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClearConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = ClearConfig.defaults();

    assertTrue(!snap.cleared().isEmpty());
    assertTrue(!snap.clearedOther().isEmpty());
    assertTrue(!snap.empty().isEmpty());
    assertTrue(!snap.emptyOther().isEmpty());
  }

  @Test
  void defaultsClearArmorIsFalse() {
    var snap = ClearConfig.defaults();

    assertFalse(snap.clearArmor());
  }

  @Test
  void whenClearedReturnsPair() {
    var snap = ClearConfig.defaults();
    var pair = snap.whenCleared();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void whenEmptyReturnsPair() {
    var snap = ClearConfig.defaults();
    var pair = snap.whenEmpty();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }
}
