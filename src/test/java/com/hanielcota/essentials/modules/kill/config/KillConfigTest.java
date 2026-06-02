package com.hanielcota.essentials.modules.kill.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KillConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = KillConfig.defaults();

    assertTrue(!snap.killed().isEmpty());
    assertTrue(!snap.killedOther().isEmpty());
    assertTrue(!snap.alreadyDead().isEmpty());
    assertTrue(!snap.alreadyDeadOther().isEmpty());
    assertTrue(!snap.exempt().isEmpty());
    assertTrue(!snap.exemptPermission().isEmpty());
  }

  @Test
  void whenKilledReturnsPair() {
    var snap = KillConfig.defaults();
    var pair = snap.whenKilled();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void whenAlreadyDeadReturnsPair() {
    var snap = KillConfig.defaults();
    var pair = snap.whenAlreadyDead();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void formatExemptReplacesPlayer() {
    var snap = KillConfig.defaults();
    var msg = snap.formatExempt("Steve");

    assertTrue(msg.contains("Steve"));
  }
}
