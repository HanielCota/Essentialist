package com.hanielcota.essentials.modules.sudo.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SudoConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = SudoConfig.defaults();

    assertTrue(!snap.executed().isEmpty());
    assertTrue(!snap.emptyCommand().isEmpty());
  }

  @Test
  void formatExecutedReplacesPlaceholders() {
    var snap = SudoConfig.defaults();
    var msg = snap.formatExecuted("Steve", "fly");

    assertTrue(msg.contains("Steve"));
    assertTrue(msg.contains("fly"));
  }
}
