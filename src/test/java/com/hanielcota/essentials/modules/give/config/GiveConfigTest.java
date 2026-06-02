package com.hanielcota.essentials.modules.give.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GiveConfigTest {

  @Test
  void defaultsHaveSensibleMaxAmount() {
    var snap = GiveConfig.defaults();

    assertTrue(snap.maxAmount() > 0);
  }

  @Test
  void whenGivenReturnsMessagePairWithBothTemplates() {
    var snap = GiveConfig.defaults();
    var pair = snap.whenGiven();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
    assertTrue(!pair.forTarget("Player").isEmpty());
  }

  @Test
  void whenPartialReturnsMessagePairWithBothTemplates() {
    var snap = GiveConfig.defaults();
    var pair = snap.whenPartial();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
    assertTrue(!pair.forTarget("Player").isEmpty());
  }

  @Test
  void whenInventoryFullReturnsMessagePairWithBothTemplates() {
    var snap = GiveConfig.defaults();
    var pair = snap.whenInventoryFull();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
    assertTrue(!pair.forTarget("Player").isEmpty());
  }

  @Test
  void formatAmountTooLargeReplacesMaxPlaceholder() {
    var snap = GiveConfig.defaults();
    var formatted = snap.formatAmountTooLarge();

    var maxStr = Integer.toString(snap.maxAmount());
    assertTrue(formatted.contains(maxStr));
  }

  @Test
  void formatGivenAllReplacesAllPlaceholders() {
    var snap = GiveConfig.defaults();
    var formatted = snap.formatGivenAll("stone", 64, 3);

    assertTrue(formatted.contains("stone"));
    assertTrue(formatted.contains("64"));
    assertTrue(formatted.contains("3"));
  }

  @Test
  void customMaxAmountIsRespected() {
    var snap =
        new GiveConfig(
            "given",
            "givenOther",
            "partial",
            "partialOther",
            "full",
            "fullOther",
            "invalid",
            "invalidAmount",
            "tooLarge {max}",
            100,
            "all");

    assertEquals(100, snap.maxAmount());
  }
}
