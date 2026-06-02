package com.hanielcota.essentials.modules.title.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TitleConfigTest {

  @Test
  void defaultsHavePositiveTimings() {
    var snap = TitleConfig.defaults();

    assertTrue(snap.fadeInTicks() > 0);
    assertTrue(snap.stayTicks() > 0);
    assertTrue(snap.fadeOutTicks() > 0);
  }

  @Test
  void defaultsHaveAllMessages() {
    var snap = TitleConfig.defaults();

    assertTrue(!snap.sent().isEmpty());
    assertTrue(!snap.sentOther().isEmpty());
    assertTrue(!snap.broadcasted().isEmpty());
    assertTrue(!snap.noPermissionOther().isEmpty());
    assertTrue(!snap.usage().isEmpty());
    assertTrue(!snap.targetOffline().isEmpty());
  }

  @Test
  void whenSentReturnsPair() {
    var snap = TitleConfig.defaults();
    var pair = snap.whenSent();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void formatBroadcastedReplacesCount() {
    var snap = TitleConfig.defaults();
    var msg = snap.formatBroadcasted(10);

    assertTrue(msg.contains("10"));
  }

  @Test
  void formatTargetOfflineReplacesPlayer() {
    var snap = TitleConfig.defaults();
    var msg = snap.formatTargetOffline("Steve");

    assertTrue(msg.contains("Steve"));
  }
}
