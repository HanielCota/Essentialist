package com.hanielcota.essentials.modules.rename.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class RenameConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = RenameConfig.defaults();

    assertTrue(!snap.emptyHand().isEmpty());
    assertTrue(!snap.renamed().isEmpty());
    assertTrue(!snap.cleared().isEmpty());
  }

  @Test
  void formatRenamedReplacesNamePlaceholder() {
    var snap = RenameConfig.defaults();
    var msg = snap.formatRenamed("Sword of Power");

    assertTrue(msg.contains("Sword of Power"));
  }
}
