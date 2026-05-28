package com.hanielcota.essentials.modules.warps.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class WarpOccupancyTest {

  @Test
  void enterRecordsAndCounts() {
    var occupancy = new WarpOccupancy();
    var player = UUID.randomUUID();

    occupancy.enter(player, "spawn", "world", 0, 64, 0);

    assertEquals(1, occupancy.count("spawn"));
    assertTrue(occupancy.isTracked(player));
  }

  @Test
  void enteringANewWarpMovesThePlayer() {
    var occupancy = new WarpOccupancy();
    var player = UUID.randomUUID();

    occupancy.enter(player, "spawn", "world", 0, 64, 0);
    occupancy.enter(player, "pvp", "world", 100, 64, 100);

    assertEquals(0, occupancy.count("spawn"));
    assertEquals(1, occupancy.count("pvp"));
  }

  @Test
  void leaveDropsThePlayer() {
    var occupancy = new WarpOccupancy();
    var player = UUID.randomUUID();

    occupancy.enter(player, "spawn", "world", 0, 64, 0);
    occupancy.leave(player);

    assertEquals(0, occupancy.count("spawn"));
    assertFalse(occupancy.isTracked(player));
  }

  @Test
  void unknownWarpCountsZero() {
    assertEquals(0, new WarpOccupancy().count("ghost"));
  }

  @Test
  void isOutsideAnchorChecksDistanceAndWorld() {
    var occupancy = new WarpOccupancy();
    var player = UUID.randomUUID();
    occupancy.enter(player, "spawn", "world", 0, 64, 0);

    assertFalse(occupancy.isOutsideAnchor(player, "world", 5, 64, 0, 10));
    assertTrue(occupancy.isOutsideAnchor(player, "world", 50, 64, 0, 10));
    assertTrue(occupancy.isOutsideAnchor(player, "world_nether", 0, 64, 0, 10));
  }

  @Test
  void untrackedPlayerIsNeverOutside() {
    var occupancy = new WarpOccupancy();
    assertFalse(occupancy.isOutsideAnchor(UUID.randomUUID(), "world", 0, 64, 0, 10));
  }
}
