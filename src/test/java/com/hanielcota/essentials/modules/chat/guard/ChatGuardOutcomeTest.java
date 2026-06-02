package com.hanielcota.essentials.modules.chat.guard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ChatGuardOutcomeTest {

  @Test
  void twoValuesOnly() {
    assertEquals(2, ChatGuardOutcome.values().length);
  }
}
