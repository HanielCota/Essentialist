package com.hanielcota.essentials.modules.socialspy.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SocialSpyConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = SocialSpyConfig.defaults();

    assertTrue(!snap.spyFormat().isEmpty());
    assertTrue(!snap.enabled().isEmpty());
    assertTrue(!snap.disabled().isEmpty());
    assertTrue(!snap.enabledOther().isEmpty());
    assertTrue(!snap.disabledOther().isEmpty());
  }

  @Test
  void spyFormatHasExpectedPlaceholders() {
    var snap = SocialSpyConfig.defaults();
    var fmt = snap.spyFormat();

    assertTrue(fmt.contains("{sender}"));
    assertTrue(fmt.contains("{target}"));
    assertTrue(fmt.contains("{message}"));
  }
}
