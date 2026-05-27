package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.api.TeleportsApi;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import org.junit.jupiter.api.Test;

class TeleportsApiAdapterTest {

  /**
   * Smoke test only — the live {@link TeleportService#toPlayer} path requires a Player + Bukkit
   * server and is exercised by integration tests on a real server. Here we just confirm the service
   * implements {@link TeleportsApi} and is constructible.
   */
  @Test
  void serviceImplementsTeleportsApiAndIsConstructible() {
    var service = new TeleportService();

    assertNotNull(service);
    assertTrue(service instanceof TeleportsApi);
  }
}
