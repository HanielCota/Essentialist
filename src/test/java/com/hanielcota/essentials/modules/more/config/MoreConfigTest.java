package com.hanielcota.essentials.modules.more.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MoreConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = MoreConfig.defaults();

    assertTrue(!snap.filled().isEmpty());
    assertTrue(!snap.emptyHand().isEmpty());
    assertTrue(!snap.alreadyFull().isEmpty());
  }
}
