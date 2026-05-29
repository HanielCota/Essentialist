package com.hanielcota.essentials.modules.kit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.kit.domain.Kit;
import com.hanielcota.essentials.modules.kit.repository.KitUsageRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class KitCooldownServiceTest {

  private static final UUID PLAYER = UUID.randomUUID();

  @Test
  void readyWhenNeverClaimed() {
    var clock = new AtomicLong(10_000L);
    var service = new KitCooldownService(new FakeUsage(), clock::get);
    var kit = kit(60);

    assertEquals(0, service.remainingSeconds(PLAYER, kit));
    assertFalse(service.hasClaimed(PLAYER, kit));
  }

  @Test
  void cooldownCountsDownAndExpires() {
    var clock = new AtomicLong(0L);
    var service = new KitCooldownService(new FakeUsage(), clock::get);
    var kit = kit(60);

    service.markClaimed(PLAYER, kit);
    assertTrue(service.hasClaimed(PLAYER, kit));
    assertEquals(60, service.remainingSeconds(PLAYER, kit));

    clock.set(45_000L);
    assertEquals(15, service.remainingSeconds(PLAYER, kit));

    clock.set(60_000L);
    assertEquals(0, service.remainingSeconds(PLAYER, kit));
  }

  @Test
  void noCooldownKitIsAlwaysReadyButStillTracksClaims() {
    var clock = new AtomicLong(0L);
    var service = new KitCooldownService(new FakeUsage(), clock::get);
    var kit = kit(0);

    service.markClaimed(PLAYER, kit);

    assertEquals(0, service.remainingSeconds(PLAYER, kit));
    assertTrue(service.hasClaimed(PLAYER, kit));
  }

  private static Kit kit(long cooldownSeconds) {
    return new Kit(
        "starter",
        "Starter",
        Material.CHEST,
        "default",
        cooldownSeconds,
        false,
        "",
        false,
        List.of());
  }

  private static final class FakeUsage implements KitUsageRepository {
    private final Map<String, Long> store = new HashMap<>();

    @Override
    public Map<String, Long> findAll(UUID player) {
      return Map.copyOf(this.store);
    }

    @Override
    public void upsert(UUID player, String kitId, long usedAtMs) {
      this.store.put(kitId, usedAtMs);
    }

    @Override
    public void deleteKit(String kitId) {
      this.store.remove(kitId);
    }
  }
}
