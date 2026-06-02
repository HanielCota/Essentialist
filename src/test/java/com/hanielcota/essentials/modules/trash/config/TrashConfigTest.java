package com.hanielcota.essentials.modules.trash.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TrashConfigTest {

  @Test
  void defaultsHavePositiveRows() {
    var snap = TrashConfig.defaults();

    assertTrue(snap.rows() > 0);
  }

  @Test
  void sizeIsRowsTimesNine() {
    var snap = new TrashConfig(4, "Trash");

    assertEquals(36, snap.size());
  }

  @Test
  void sizeClampsRowsToSix() {
    var snap = new TrashConfig(10, "Trash");

    assertEquals(54, snap.size());
  }

  @Test
  void sizeClampsRowsToOne() {
    var snap = new TrashConfig(0, "Trash");

    assertEquals(9, snap.size());
  }

  @Test
  void defaultsHaveTitle() {
    var snap = TrashConfig.defaults();

    assertTrue(!snap.title().isEmpty());
  }
}
