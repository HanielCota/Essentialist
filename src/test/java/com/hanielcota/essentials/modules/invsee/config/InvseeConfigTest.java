package com.hanielcota.essentials.modules.invsee.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InvseeConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = InvseeConfig.defaults();

    assertTrue(!snap.menuTitle().isEmpty());
    assertTrue(!snap.opened().isEmpty());
    assertTrue(!snap.self().isEmpty());
    assertTrue(!snap.alreadyViewed().isEmpty());
  }
}
