package com.hanielcota.essentials.modules.silencer.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SilencerConfigTest {

  @Test
  void defaultsEnableAllSuppression() {
    var snap = SilencerConfig.defaults();

    assertTrue(snap.suppressJoin());
    assertTrue(snap.suppressQuit());
    assertTrue(snap.suppressDeath());
    assertTrue(snap.suppressAdvancement());
  }

  @Test
  void canDisableIndividualFlags() {
    var snap = new SilencerConfig(true, false, true, false);

    assertTrue(snap.suppressJoin());
    assertTrue(!snap.suppressQuit());
    assertTrue(snap.suppressDeath());
    assertTrue(!snap.suppressAdvancement());
  }
}
