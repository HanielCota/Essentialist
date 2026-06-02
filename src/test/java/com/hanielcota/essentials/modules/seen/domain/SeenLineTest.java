package com.hanielcota.essentials.modules.seen.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SeenLineTest {

  @Test
  void constructsWithAllFields() {
    var line = new SeenLine(SeenLine.Kind.ONLINE, "Steve", "5m");

    assertEquals(SeenLine.Kind.ONLINE, line.kind());
    assertEquals("Steve", line.displayName());
    assertEquals("5m", line.duration());
  }

  @Test
  void kindHasTwoValues() {
    assertEquals(2, SeenLine.Kind.values().length);
  }

  @Test
  void offlineKindExists() {
    assertNotNull(SeenLine.Kind.valueOf("OFFLINE"));
  }
}
