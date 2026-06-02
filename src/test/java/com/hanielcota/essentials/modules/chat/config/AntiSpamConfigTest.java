package com.hanielcota.essentials.modules.chat.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AntiSpamConfigTest {

  @Test
  void defaultsEnableRepeatBlocking() {
    var snap = AntiSpamConfig.defaults();

    assertTrue(snap.blockRepeated());
  }

  @Test
  void formatCooldownWarningReplacesSecondsPlaceholder() {
    var snap = AntiSpamConfig.defaults();
    var formatted = snap.formatCooldownWarning(5);

    assertTrue(formatted.contains("5"));
    assertTrue(formatted.contains("s"));
  }

  @Test
  void formatCooldownWarningReplacesLargeSeconds() {
    var snap = AntiSpamConfig.defaults();
    var formatted = snap.formatCooldownWarning(120);

    assertTrue(formatted.contains("120"));
  }

  @Test
  void repeatedWarningIsNotEmptyInDefaults() {
    var snap = AntiSpamConfig.defaults();

    assertTrue(!snap.repeatedWarning().isEmpty());
  }

  @Test
  void emptyRepeatedWarningDoesNotCrashFormatting() {
    var snap = new AntiSpamConfig(true, "<red>{seconds}s", "");

    assertEquals("", snap.repeatedWarning());
  }
}
