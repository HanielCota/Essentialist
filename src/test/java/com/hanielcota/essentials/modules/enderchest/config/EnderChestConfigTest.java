package com.hanielcota.essentials.modules.enderchest.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EnderChestConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = EnderChestConfig.defaults();

    assertTrue(!snap.opened().isEmpty());
    assertTrue(!snap.openedOther().isEmpty());
    assertTrue(!snap.noPermissionOther().isEmpty());
  }

  @Test
  void whenOpenedReturnsPair() {
    var snap = EnderChestConfig.defaults();
    var pair = snap.whenOpened();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
    assertTrue(!pair.forTarget("Player").isEmpty());
  }
}
