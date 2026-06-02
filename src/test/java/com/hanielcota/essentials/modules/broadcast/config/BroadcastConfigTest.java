package com.hanielcota.essentials.modules.broadcast.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BroadcastConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = BroadcastConfig.defaults();

    assertTrue(!snap.format().isEmpty());
    assertTrue(!snap.usage().isEmpty());
  }

  @Test
  void formatLineReplacesMessagePlaceholder() {
    var snap = BroadcastConfig.defaults();
    var line = snap.formatLine("Hello world");

    assertTrue(line.contains("Hello world"));
  }
}
