package com.hanielcota.essentials.modules.enchant.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class EnchantConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = EnchantConfig.defaults();

    assertTrue(!snap.emptyHand().isEmpty());
    assertTrue(!snap.applied().isEmpty());
    assertTrue(!snap.removed().isEmpty());
    assertTrue(!snap.notEnchanted().isEmpty());
    assertTrue(!snap.cleared().isEmpty());
    assertTrue(!snap.nothingToClear().isEmpty());
    assertTrue(!snap.invalidLevel().isEmpty());
    assertTrue(!snap.levelTooHigh().isEmpty());
    assertTrue(!snap.blocked().isEmpty());
    assertTrue(!snap.incompatible().isEmpty());
  }

  @Test
  void defaultsAllowUnsafeEnabled() {
    var snap = EnchantConfig.defaults();

    assertTrue(snap.allowUnsafe());
  }

  @Test
  void defaultsBlockedListEmpty() {
    var snap = EnchantConfig.defaults();

    assertTrue(snap.blockedEnchantments().isEmpty());
  }

  @Test
  void isBlockedReturnsTrueForBlockedEnchantment() {
    var snap =
        new EnchantConfig(
            10,
            true,
            List.of("sharpness", "mending"),
            "eh",
            "applied",
            "removed",
            "notEnch",
            "cleared",
            "nothing",
            "invalid",
            "tooHigh",
            "blocked",
            "incomp");

    assertTrue(snap.isBlocked("sharpness"));
    assertTrue(snap.isBlocked("SHARPNESS"));
    assertTrue(snap.isBlocked("mending"));
  }

  @Test
  void isBlockedReturnsFalseForUnknownEnchantment() {
    var snap =
        new EnchantConfig(
            10,
            true,
            List.of("sharpness"),
            "eh",
            "applied",
            "removed",
            "notEnch",
            "cleared",
            "nothing",
            "invalid",
            "tooHigh",
            "blocked",
            "incomp");

    assertFalse(snap.isBlocked("efficiency"));
  }

  @Test
  void formatAppliedReplacesPlaceholders() {
    var snap = EnchantConfig.defaults();
    var msg = snap.formatApplied("sharpness", 5);

    assertTrue(msg.contains("sharpness"));
    assertTrue(msg.contains("5"));
  }

  @Test
  void formatRemovedReplacesPlaceholder() {
    var snap = EnchantConfig.defaults();
    var msg = snap.formatRemoved("sharpness");

    assertTrue(msg.contains("sharpness"));
  }

  @Test
  void formatNotEnchantedReplacesPlaceholder() {
    var snap = EnchantConfig.defaults();
    var msg = snap.formatNotEnchanted("mending");

    assertTrue(msg.contains("mending"));
  }

  @Test
  void formatClearedReplacesCount() {
    var snap = EnchantConfig.defaults();
    var msg = snap.formatCleared(3);

    assertTrue(msg.contains("3"));
  }

  @Test
  void formatLevelTooHighReplacesMax() {
    var snap = EnchantConfig.defaults();
    var msg = snap.formatLevelTooHigh(10);

    assertTrue(msg.contains("10"));
  }

  @Test
  void formatBlockedReplacesEnchantment() {
    var snap = EnchantConfig.defaults();
    var msg = snap.formatBlocked("mending");

    assertTrue(msg.contains("mending"));
  }

  @Test
  void formatIncompatibleReplacesEnchantment() {
    var snap = EnchantConfig.defaults();
    var msg = snap.formatIncompatible("sharpness");

    assertTrue(msg.contains("sharpness"));
  }
}
