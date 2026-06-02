package com.hanielcota.essentials.modules.compact.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CompactConfigTest {

  @Test
  void defaultsHaveRecipes() {
    var snap = CompactConfig.defaults();

    assertFalse(snap.recipes().isEmpty());
  }

  @Test
  void defaultsHaveMessages() {
    var snap = CompactConfig.defaults();

    assertTrue(!snap.nothing().isEmpty());
    assertTrue(!snap.success().isEmpty());
  }

  @Test
  void formatSuccessReplacesCount() {
    var snap = CompactConfig.defaults();
    var msg = snap.formatSuccess(5);

    assertTrue(msg.contains("5"));
  }
}
