package com.hanielcota.essentials.modules.hat.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class HatConfigTest {

  @Test
  void defaultsHaveWhitelistWithItems() {
    var snap = HatConfig.defaults();

    assertFalse(snap.materialWhitelist().isEmpty());
  }

  @Test
  void isAllowedReturnsTrueForItemInWhitelist() {
    var snap = HatConfig.defaults();

    assertTrue(snap.isAllowed(Material.LEATHER_HELMET));
  }

  @Test
  void isAllowedReturnsFalseForItemNotInWhitelist() {
    var snap = HatConfig.defaults();

    assertFalse(snap.isAllowed(Material.DIRT));
  }

  @Test
  void emptyWhitelistAllowsEverything() {
    var snap = new HatConfig("equipped", "empty", "notAllowed", "full", List.of());

    assertTrue(snap.isAllowed(Material.DIRT));
    assertTrue(snap.isAllowed(Material.LEATHER_HELMET));
  }

  @Test
  void allMessagesAreNonEmptyInDefaults() {
    var snap = HatConfig.defaults();

    assertTrue(!snap.equipped().isEmpty());
    assertTrue(!snap.emptyHand().isEmpty());
    assertTrue(!snap.notAllowed().isEmpty());
    assertTrue(!snap.inventoryFull().isEmpty());
  }
}
