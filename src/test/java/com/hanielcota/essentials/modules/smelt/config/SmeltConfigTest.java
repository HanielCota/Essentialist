package com.hanielcota.essentials.modules.smelt.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SmeltConfigTest {

  @Test
  void defaultsHaveMappings() {
    var snap = SmeltConfig.defaults();

    assertFalse(snap.mappings().isEmpty());
  }

  @Test
  void defaultsHaveMessages() {
    var snap = SmeltConfig.defaults();

    assertTrue(!snap.nothing().isEmpty());
    assertTrue(!snap.success().isEmpty());
  }

  @Test
  void formatSuccessReplacesCount() {
    var snap = SmeltConfig.defaults();
    var msg = snap.formatSuccess(42);

    assertTrue(msg.contains("42"));
  }
}
