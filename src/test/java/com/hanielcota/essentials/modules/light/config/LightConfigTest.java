package com.hanielcota.essentials.modules.light.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LightConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = LightConfig.defaults();

    assertTrue(!snap.enabled().isEmpty());
    assertTrue(!snap.enabledOther().isEmpty());
    assertTrue(!snap.disabled().isEmpty());
    assertTrue(!snap.disabledOther().isEmpty());
  }
}
