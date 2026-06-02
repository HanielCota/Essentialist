package com.hanielcota.essentials.modules.itemlore.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ItemLoreConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = ItemLoreConfig.defaults();

    assertTrue(!snap.added().isEmpty());
    assertTrue(!snap.updated().isEmpty());
    assertTrue(!snap.removed().isEmpty());
    assertTrue(!snap.cleared().isEmpty());
    assertTrue(!snap.emptyHand().isEmpty());
    assertTrue(!snap.invalidLine().isEmpty());
    assertTrue(!snap.emptyLore().isEmpty());
    assertTrue(!snap.usage().isEmpty());
  }
}
