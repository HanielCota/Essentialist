package com.hanielcota.essentials.modules.teleport.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TeleportConfigTest {

  @Test
  void defaultsHavePositiveHistoryDepth() {
    var snap = TeleportConfig.defaults();

    assertTrue(snap.historyDepth() > 0);
  }

  @Test
  void formatToPlayerReplacesPlaceholder() {
    var snap = TeleportConfig.defaults();
    var msg = snap.formatToPlayer("Steve");

    assertTrue(msg.contains("Steve"));
  }

  @Test
  void formatTeleportedToReplacesPlaceholder() {
    var snap = TeleportConfig.defaults();
    var msg = snap.formatTeleportedTo("Alex");

    assertTrue(msg.contains("Alex"));
  }

  @Test
  void formatMoveSenderReplacesBothPlaceholders() {
    var snap = TeleportConfig.defaults();
    var msg = snap.formatMoveSender("Alice", "Bob");

    assertTrue(msg.contains("Alice"));
    assertTrue(msg.contains("Bob"));
  }

  @Test
  void formatMoveNotifyReplacesPlaceholder() {
    var snap = TeleportConfig.defaults();
    var msg = snap.formatMoveNotify("Steve");

    assertTrue(msg.contains("Steve"));
  }

  @Test
  void formatToPosReplacesCoordinates() {
    var snap = TeleportConfig.defaults();
    var msg = snap.formatToPos(100.5, 64.0, -200.0);

    assertTrue(msg.contains("100.5") || msg.contains("100"));
    assertTrue(msg.contains("64"));
    assertTrue(msg.contains("-200") || msg.contains("200"));
  }

  @Test
  void formatBroughtPlayerReplacesPlaceholder() {
    var snap = TeleportConfig.defaults();
    var msg = snap.formatBroughtPlayer("Steve");

    assertTrue(msg.contains("Steve"));
  }

  @Test
  void formatBroughtByReplacesPlaceholder() {
    var snap = TeleportConfig.defaults();
    var msg = snap.formatBroughtBy("Alex");

    assertTrue(msg.contains("Alex"));
  }

  @Test
  void formatPlayerNotFoundReplacesPlaceholder() {
    var snap = TeleportConfig.defaults();
    var msg = snap.formatPlayerNotFound("Ghost");

    assertTrue(msg.contains("Ghost"));
  }

  @Test
  void selfTargetMessageIsNotEmpty() {
    var snap = TeleportConfig.defaults();

    assertTrue(!snap.selfTarget().isEmpty());
  }

  @Test
  void invalidPositionMessageIsNotEmpty() {
    var snap = TeleportConfig.defaults();

    assertTrue(!snap.invalidPosition().isEmpty());
  }

  @Test
  void mustBePlayerMessageIsNotEmpty() {
    var snap = TeleportConfig.defaults();

    assertTrue(!snap.mustBePlayer().isEmpty());
  }

  @Test
  void customHistoryDepth() {
    var snap =
        new TeleportConfig(
            10,
            "tp",
            "teleportedTo",
            "self",
            "moveSender",
            "moveNotify",
            "toPos",
            "brought",
            "broughtBy",
            "failed",
            "invalidPos",
            "notFound",
            "mustBePlayer",
            "noPerm",
            "cancelNoPending");

    assertEquals(10, snap.historyDepth());
  }
}
