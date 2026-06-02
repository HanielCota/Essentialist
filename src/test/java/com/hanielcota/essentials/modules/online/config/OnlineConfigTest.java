package com.hanielcota.essentials.modules.online.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OnlineConfigTest {

  @Test
  void formatZeroUsesEmptyTemplate() {
    var snap = OnlineConfig.defaults();
    var msg = snap.format(0, 20);

    assertTrue(msg.contains("nobody"));
  }

  @Test
  void formatOneUsesSingularTemplate() {
    var snap = OnlineConfig.defaults();
    var msg = snap.format(1, 20);

    assertTrue(msg.contains("1"));
    assertTrue(msg.contains("20"));
    assertTrue(msg.contains("is"));
  }

  @Test
  void formatTwoOrMoreUsesPluralTemplate() {
    var snap = OnlineConfig.defaults();
    var msg = snap.format(5, 20);

    assertTrue(msg.contains("5"));
    assertTrue(msg.contains("20"));
    assertTrue(msg.contains("are"));
  }

  @Test
  void formatClampsCountToMax() {
    var snap = OnlineConfig.defaults();
    var msg = snap.format(100, 20);

    assertTrue(msg.contains("20"));
    assertTrue(!msg.contains("100"));
  }

  @Test
  void defaultsHaveAllMessages() {
    var snap = OnlineConfig.defaults();

    assertTrue(!snap.empty().isEmpty());
    assertTrue(!snap.singular().isEmpty());
    assertTrue(!snap.plural().isEmpty());
  }
}
