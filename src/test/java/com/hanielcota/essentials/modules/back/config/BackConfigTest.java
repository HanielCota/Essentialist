package com.hanielcota.essentials.modules.back.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BackConfigTest {

  @Test
  void defaultsHaveMenuConfig() {
    var snap = BackConfig.defaults();

    assertTrue(snap.menuRows() > 0);
    assertTrue(!snap.menuContentSlots().isEmpty());
    assertTrue(!snap.menuTitle().isEmpty());
  }

  @Test
  void defaultsHaveMessages() {
    var snap = BackConfig.defaults();

    assertTrue(!snap.back().isEmpty());
    assertTrue(!snap.noBack().isEmpty());
  }

  @Test
  void formatItemNameReplacesIndex() {
    var snap = BackConfig.defaults();
    var msg = snap.formatItemName(1);

    assertTrue(msg.contains("1"));
  }

  @Test
  void formatBackReplacesPlaceholders() {
    var snap = BackConfig.defaults();
    var msg = snap.formatBack("world", 100.0, 64.0, -200.0);

    assertTrue(msg.contains("world"));
  }

  @Test
  void timeFormatterReturnsValidFormatter() {
    var snap = BackConfig.defaults();
    var formatter = snap.timeFormatter();

    assertTrue(formatter != null);
  }

  @Test
  void itemLoreHasEntries() {
    var snap = BackConfig.defaults();

    assertTrue(!snap.itemLore().isEmpty());
  }

  @Test
  void navigationButtonsAreNonNull() {
    var snap = BackConfig.defaults();

    assertTrue(snap.navigation() != null);
  }
}
