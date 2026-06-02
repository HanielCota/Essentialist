package com.hanielcota.essentials.modules.ping.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PingConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = PingConfig.defaults();

    assertTrue(!snap.ownPing().isEmpty());
    assertTrue(!snap.otherPing().isEmpty());
  }

  @Test
  void messageReturnsPair() {
    var snap = PingConfig.defaults();
    var pair = snap.message();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void goodMaxIsLessThanMediumMax() {
    var snap = PingConfig.defaults();

    assertTrue(snap.goodMaxPing() < snap.mediumMaxPing());
  }
}
