package com.hanielcota.essentials.modules.kick.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KickConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = KickConfig.defaults();

    assertTrue(!snap.defaultReason().isEmpty());
    assertTrue(!snap.screen().isEmpty());
    assertTrue(!snap.kicked().isEmpty());
    assertTrue(!snap.exempt().isEmpty());
    assertTrue(!snap.exemptPermission().isEmpty());
  }

  @Test
  void reasonOrReturnsDefaultWhenBlank() {
    var snap = KickConfig.defaults();

    assertEquals(snap.defaultReason(), snap.reasonOr("   "));
  }

  @Test
  void reasonOrReturnsProvidedWhenNonBlank() {
    var snap = KickConfig.defaults();

    assertEquals("Spamming", snap.reasonOr("Spamming"));
  }

  @Test
  void formatScreenReplacesReason() {
    var snap = KickConfig.defaults();
    var msg = snap.formatScreen("Spamming");

    assertTrue(msg.contains("Spamming"));
  }

  @Test
  void formatKickedReplacesPlayerPlaceholder() {
    var snap = KickConfig.defaults();
    var msg = snap.formatKicked("Steve", "Spamming");

    assertTrue(msg.contains("Steve"));
  }

  @Test
  void formatExemptReplacesPlayer() {
    var snap = KickConfig.defaults();
    var msg = snap.formatExempt("Steve");

    assertTrue(msg.contains("Steve"));
  }
}
