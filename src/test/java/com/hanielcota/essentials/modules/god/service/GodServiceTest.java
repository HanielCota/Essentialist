package com.hanielcota.essentials.modules.god.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class GodServiceTest {

  @Test
  void toggleFlipsStateAndReportsNewValue() {
    var service = new GodService();
    var id = UUID.randomUUID();

    assertTrue(service.toggle(id));
    assertTrue(service.isGod(id));

    assertFalse(service.toggle(id));
    assertFalse(service.isGod(id));
  }

  @Test
  void enableAndDisableReportTransitionsOnce() {
    var service = new GodService();
    var id = UUID.randomUUID();

    assertTrue(service.enable(id));
    assertFalse(service.enable(id));

    assertTrue(service.disable(id));
    assertFalse(service.disable(id));
  }

  @Test
  void forgetClearsState() {
    var service = new GodService();
    var id = UUID.randomUUID();
    service.enable(id);

    service.forget(id);

    assertFalse(service.isGod(id));
  }
}
