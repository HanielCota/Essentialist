package com.hanielcota.essentials.modules.near.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NearConfigTest {

  @Test
  void defaultsHavePositiveRange() {
    var snap = NearConfig.defaults();

    assertTrue(snap.defaultRadius() > 0);
    assertTrue(snap.maxRadius() > snap.defaultRadius());
  }

  @Test
  void defaultsHaveAllMessages() {
    var snap = NearConfig.defaults();

    assertTrue(!snap.found().isEmpty());
    assertTrue(!snap.entry().isEmpty());
    assertTrue(!snap.separator().isEmpty());
    assertTrue(!snap.none().isEmpty());
    assertTrue(!snap.invalidRadius().isEmpty());
  }

  @Test
  void formatEntryReplacesPlaceholders() {
    var snap = NearConfig.defaults();
    var msg = snap.formatEntry("Steve", 42);

    assertTrue(msg.contains("Steve"));
    assertTrue(msg.contains("42"));
  }

  @Test
  void formatFoundReplacesPlaceholders() {
    var snap = NearConfig.defaults();
    var msg = snap.formatFound(100, 3, "Steve, Alex, Bob");

    assertTrue(msg.contains("100"));
    assertTrue(msg.contains("3"));
    assertTrue(msg.contains("Steve, Alex, Bob"));
  }

  @Test
  void formatNoneReplacesRadius() {
    var snap = NearConfig.defaults();
    var msg = snap.formatNone(50);

    assertTrue(msg.contains("50"));
  }

  @Test
  void formatInvalidRadiusReplacesMax() {
    var snap = NearConfig.defaults();
    var msg = snap.formatInvalidRadius();

    assertTrue(msg.contains(Integer.toString(snap.maxRadius())));
  }
}
