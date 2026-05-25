package com.hanielcota.essentials.modules.vanish.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class VanishServiceTest {

  @Test
  void enterReportsTransitionOnlyOnce() {
    var service = new VanishService();
    var id = UUID.randomUUID();

    assertTrue(service.enter(id));
    assertFalse(service.enter(id));
    assertTrue(service.isVanished(id));
  }

  @Test
  void exitReportsTransitionOnlyWhenVanished() {
    var service = new VanishService();
    var id = UUID.randomUUID();

    assertFalse(service.exit(id));
    service.enter(id);
    assertTrue(service.exit(id));
    assertFalse(service.isVanished(id));
  }

  @Test
  void vanishedSnapshotIsImmutable() {
    var service = new VanishService();
    var id = UUID.randomUUID();
    service.enter(id);

    var snapshot = service.vanished();
    assertEquals(1, snapshot.size());
    assertTrue(snapshot.contains(id));

    // The snapshot is a Set.copyOf — mutations to live state do not leak in.
    service.exit(id);
    assertTrue(snapshot.contains(id));
    assertEquals(0, service.size());
  }
}
