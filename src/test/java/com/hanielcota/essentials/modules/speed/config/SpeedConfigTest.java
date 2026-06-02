package com.hanielcota.essentials.modules.speed.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SpeedConfigTest {

  @Test
  void defaultsHavePositiveRange() {
    var snap = SpeedConfig.defaults();

    assertTrue(snap.minSpeed() > 0);
    assertTrue(snap.maxSpeed() > snap.minSpeed());
  }

  @Test
  void defaultsHaveVanillaResetSpeeds() {
    var snap = SpeedConfig.defaults();

    assertEquals(0.2f, snap.resetWalkSpeed());
    assertEquals(0.1f, snap.resetFlySpeed());
  }

  @Test
  void defaultsHaveAllMessages() {
    var snap = SpeedConfig.defaults();

    assertTrue(!snap.walkSet().isEmpty());
    assertTrue(!snap.flySet().isEmpty());
    assertTrue(!snap.reset().isEmpty());
    assertTrue(!snap.invalid().isEmpty());
    assertTrue(!snap.usage().isEmpty());
  }

  @Test
  void whenWalkSetReplacesValor() {
    var snap = SpeedConfig.defaults();
    var pair = snap.whenWalkSet(5);

    assertTrue(pair.forSender(true, "Player").contains("5"));
  }

  @Test
  void whenFlySetReplacesValor() {
    var snap = SpeedConfig.defaults();
    var pair = snap.whenFlySet(8);

    assertTrue(pair.forSender(true, "Player").contains("8"));
  }

  @Test
  void whenResetReturnsPair() {
    var snap = SpeedConfig.defaults();
    var pair = snap.whenReset();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void formatInvalidReplacesMinMax() {
    var snap = SpeedConfig.defaults();
    var msg = snap.formatInvalid();

    assertTrue(msg.contains(Integer.toString(snap.minSpeed())));
    assertTrue(msg.contains(Integer.toString(snap.maxSpeed())));
  }

  @Test
  void formatUsageReplacesMinMax() {
    var snap = SpeedConfig.defaults();
    var msg = snap.formatUsage();

    assertTrue(msg.contains(Integer.toString(snap.minSpeed())));
    assertTrue(msg.contains(Integer.toString(snap.maxSpeed())));
  }
}
