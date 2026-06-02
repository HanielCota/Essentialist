package com.hanielcota.essentials.modules.actionbar.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ActionBarConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = ActionBarConfig.defaults();

    assertTrue(!snap.usage().isEmpty());
    assertTrue(!snap.playerOnly().isEmpty());
    assertTrue(!snap.sent().isEmpty());
    assertTrue(!snap.broadcasted().isEmpty());
  }

  @Test
  void formatBroadcastedReplacesCount() {
    var snap = ActionBarConfig.defaults();
    var msg = snap.formatBroadcasted(7);

    assertTrue(msg.contains("7"));
  }
}
