package com.hanielcota.essentials.modules.afk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class AfkServiceTest {

  @Test
  void enterReportsTransitionOnceThenFalseUntilExit() {
    var service = new AfkService();
    var id = UUID.randomUUID();

    assertTrue(service.enter(id, "lunch"));
    assertFalse(service.enter(id, "still lunch"));
    assertTrue(service.isAfk(id));

    assertTrue(service.exit(id));
    assertFalse(service.exit(id));
    assertFalse(service.isAfk(id));
  }

  @Test
  void stateReturnsTheLastReason() {
    var service = new AfkService();
    var id = UUID.randomUUID();

    service.enter(id, "be right back");
    var state = service.state(id).orElseThrow();

    assertEquals("be right back", state.reason());
  }

  @Test
  void recordActivityLetsLastActivityReadBack() {
    var service = new AfkService();
    var id = UUID.randomUUID();

    service.recordActivity(id, 1_234_567L);

    assertEquals(1_234_567L, service.lastActivity(id, 0L));
    assertEquals(0L, service.lastActivity(UUID.randomUUID(), 0L));
  }

  @Test
  void forgetWipesBothStateAndActivity() {
    var service = new AfkService();
    var id = UUID.randomUUID();

    service.enter(id, null);
    service.recordActivity(id, 42L);

    service.forget(id);

    assertFalse(service.isAfk(id));
    assertEquals(-1L, service.lastActivity(id, -1L));
  }
}
