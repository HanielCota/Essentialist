package com.hanielcota.essentials.modules.teleport.domain;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class TeleportOutcomeTest {

  @Test
  void allValuesArePresent() {
    assertArrayEquals(
        new TeleportOutcome[] {
          TeleportOutcome.SUCCESS,
          TeleportOutcome.FAILED,
          TeleportOutcome.SELF_TARGET,
          TeleportOutcome.INVALID_POSITION
        },
        TeleportOutcome.values());
  }
}
