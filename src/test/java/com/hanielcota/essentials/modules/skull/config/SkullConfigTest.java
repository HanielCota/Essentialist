package com.hanielcota.essentials.modules.skull.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SkullConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = SkullConfig.defaults();

    assertTrue(!snap.receivedOwn().isEmpty());
    assertTrue(!snap.receivedOther().isEmpty());
    assertTrue(!snap.playerNotFound().isEmpty());
    assertTrue(!snap.inventoryFull().isEmpty());
  }

  @Test
  void whenReceivedReturnsPair() {
    var snap = SkullConfig.defaults();
    var pair = snap.whenReceived();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
    assertTrue(!pair.forTarget("Player").isEmpty());
  }
}
