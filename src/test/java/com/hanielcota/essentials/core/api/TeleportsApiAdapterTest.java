package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.hanielcota.essentials.api.TeleportsApi;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import org.junit.jupiter.api.Test;

class TeleportsApiAdapterTest {

  /**
   * Smoke test only — the live {@link TeleportService#toPlayer} path requires a Player + Bukkit
   * server and is exercised by integration tests on a real server. Here we just confirm the adapter
   * is constructible and exposes the {@link TeleportsApi} surface.
   */
  @Test
  void adapterIsConstructibleFromAService() {
    var adapter = new TeleportsApiAdapter(new TeleportService());

    assertNotNull(adapter);
    assertEquals(TeleportsApiAdapter.class, adapter.getClass());
  }
}
