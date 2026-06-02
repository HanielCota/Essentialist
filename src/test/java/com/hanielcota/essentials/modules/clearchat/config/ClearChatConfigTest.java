package com.hanielcota.essentials.modules.clearchat.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ClearChatConfigTest {

  @Test
  void defaultsHaveAnnouncement() {
    var snap = ClearChatConfig.defaults();

    assertTrue(!snap.announcement().isEmpty());
  }

  @Test
  void defaultLinesIsPositive() {
    var snap = ClearChatConfig.defaults();

    assertTrue(snap.lines() > 0);
  }

  @Test
  void effectiveLinesClampsToRange() {
    var snap = ClearChatConfig.defaults();
    var effective = snap.effectiveLines();

    assertTrue(effective >= 0 && effective <= 300);
  }

  @Test
  void formatAnnouncementReplacesPlaceholder() {
    var snap = ClearChatConfig.defaults();
    var formatted = snap.formatAnnouncement("Steve");

    assertTrue(formatted.contains("Steve"));
  }
}
