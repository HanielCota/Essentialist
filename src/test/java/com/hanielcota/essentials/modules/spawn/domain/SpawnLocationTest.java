package com.hanielcota.essentials.modules.spawn.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SpawnLocationTest {

  @Test
  void constructsWithAllFields() {
    var worldName = "world";
    var loc = new SpawnLocation(worldName, 100.0, 64.0, -200.0, 45.0f, 0.0f);

    assertEquals(worldName, loc.world());
    assertEquals(100.0, loc.x());
    assertEquals(64.0, loc.y());
    assertEquals(-200.0, loc.z());
    assertEquals(45.0f, loc.yaw());
    assertEquals(0.0f, loc.pitch());
  }
}
