package com.hanielcota.essentials.modules.chat.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChatConfigTest {

  @Test
  void defaultsProduceNonNullSubConfigs() {
    var snap = ChatConfig.defaults();

    assertTrue(snap.global() != null);
    assertTrue(snap.local() != null);
    assertTrue(snap.staff() != null);
    assertTrue(snap.antiSpam() != null);
    assertTrue(snap.placeholders() != null);
    assertTrue(snap.messages() != null);
  }
}
