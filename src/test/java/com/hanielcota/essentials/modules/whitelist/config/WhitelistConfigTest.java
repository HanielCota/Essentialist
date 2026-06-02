package com.hanielcota.essentials.modules.whitelist.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WhitelistConfigTest {

  @Test
  void defaultsHaveMessages() {
    var snap = WhitelistConfig.defaults();
    var messages = snap.messages();

    assertNotNull(messages);
    assertTrue(!messages.added().isEmpty());
    assertTrue(!messages.removed().isEmpty());
  }
}
