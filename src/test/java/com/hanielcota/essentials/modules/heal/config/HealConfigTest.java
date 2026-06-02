package com.hanielcota.essentials.modules.heal.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HealConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = HealConfig.defaults();

    assertTrue(!snap.healed().isEmpty());
    assertTrue(!snap.healedOther().isEmpty());
    assertTrue(!snap.alreadyFull().isEmpty());
    assertTrue(!snap.alreadyFullOther().isEmpty());
    assertTrue(!snap.dead().isEmpty());
    assertTrue(!snap.deadOther().isEmpty());
    assertTrue(!snap.healedAll().isEmpty());
  }

  @Test
  void whenHealedReturnsPair() {
    var snap = HealConfig.defaults();
    var pair = snap.whenHealed();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void whenAlreadyFullReturnsPair() {
    var snap = HealConfig.defaults();
    var pair = snap.whenAlreadyFull();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void whenDeadReturnsPair() {
    var snap = HealConfig.defaults();
    var pair = snap.whenDead();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void formatHealedAllReplacesCount() {
    var snap = HealConfig.defaults();
    var msg = snap.formatHealedAll(3);

    assertTrue(msg.contains("3"));
  }
}
