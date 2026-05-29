package com.hanielcota.essentials.modules.kit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class KitDurationsTest {

  @Test
  void parsesRawSeconds() {
    assertEquals(90, KitDurations.parseSeconds("90").orElseThrow());
    assertEquals(0, KitDurations.parseSeconds("0").orElseThrow());
  }

  @Test
  void parsesUnitCombinations() {
    assertEquals(10, KitDurations.parseSeconds("10s").orElseThrow());
    assertEquals(9000, KitDurations.parseSeconds("2h30m").orElseThrow());
    assertEquals(95_410, KitDurations.parseSeconds("1d2h30m10s").orElseThrow());
  }

  @Test
  void ignoresCommasAndSpacesAndCase() {
    assertEquals(86_470, KitDurations.parseSeconds("1D,1m,10s").orElseThrow());
    assertEquals(3_660, KitDurations.parseSeconds("1h 1m").orElseThrow());
  }

  @Test
  void rejectsMalformedInput() {
    assertTrue(KitDurations.parseSeconds("").isEmpty());
    assertTrue(KitDurations.parseSeconds("abc").isEmpty());
    assertTrue(KitDurations.parseSeconds("1d2").isEmpty());
    assertTrue(KitDurations.parseSeconds("1x").isEmpty());
  }
}
