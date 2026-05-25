package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.vanish.service.VanishService;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class VanishApiAdapterTest {

  @Test
  void isVanishedDelegatesToService() {
    var service = new VanishService();
    var adapter = new VanishApiAdapter(service);
    var id = UUID.randomUUID();

    assertFalse(adapter.isVanished(id));

    service.enter(id);
    assertTrue(adapter.isVanished(id));
  }

  @Test
  void vanishedReturnsSnapshotOfServiceState() {
    var service = new VanishService();
    var adapter = new VanishApiAdapter(service);
    var first = UUID.randomUUID();
    var second = UUID.randomUUID();

    service.enter(first);
    service.enter(second);

    assertEquals(Set.of(first, second), adapter.vanished());
  }
}
