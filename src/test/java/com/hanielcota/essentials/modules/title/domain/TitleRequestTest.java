package com.hanielcota.essentials.modules.title.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class TitleRequestTest {

  @Test
  void constructsWithAllFields() {
    var id = UUID.randomUUID();
    var request = new TitleRequest(id, "Steve", "\"Hello\" \"World\"");

    assertEquals(id, request.targetId());
    assertEquals("Steve", request.targetName());
    assertEquals("\"Hello\" \"World\"", request.message());
  }

  @Test
  void selfTargetHasNullIdAndName() {
    var request = new TitleRequest(null, null, "Hello");

    assertNull(request.targetId());
    assertNull(request.targetName());
    assertEquals("Hello", request.message());
  }
}
