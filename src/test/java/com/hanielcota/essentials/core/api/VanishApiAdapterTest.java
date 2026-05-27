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
    var api = new VanishService();
    var id = UUID.randomUUID();

    assertFalse(api.isVanished(id));

    api.enter(id);
    assertTrue(api.isVanished(id));
  }

  @Test
  void vanishedReturnsSnapshotOfServiceState() {
    var api = new VanishService();
    var first = UUID.randomUUID();
    var second = UUID.randomUUID();

    api.enter(first);
    api.enter(second);

    assertEquals(Set.of(first, second), api.vanished());
  }
}
