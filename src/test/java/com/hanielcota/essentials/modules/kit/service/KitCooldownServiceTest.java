package com.hanielcota.essentials.modules.kit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.kit.domain.Kit;
import com.hanielcota.essentials.modules.kit.repository.KitUsageRepository;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class KitCooldownServiceTest {

  private static final UUID PLAYER = UUID.randomUUID();
  private static final long MILLIS_PER_DAY = 86_400_000L;

  @Test
  void readyWhenNeverClaimed() {
    var clock = new AtomicLong(10_000L);
    var service = service(new FakeUsage(), clock);
    var kit = rollingKit(60);

    assertEquals(0, service.remainingSeconds(PLAYER, kit));
    assertFalse(service.hasClaimed(PLAYER, kit));
  }

  @Test
  void rollingCooldownCountsDownAndExpires() {
    var clock = new AtomicLong(0L);
    var service = service(new FakeUsage(), clock);
    var kit = rollingKit(60);

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
    var service = service(new FakeUsage(), clock);
    var kit = rollingKit(0);

    service.markClaimed(PLAYER, kit);

    assertEquals(0, service.remainingSeconds(PLAYER, kit));
    assertTrue(service.hasClaimed(PLAYER, kit));
  }

  @Test
  void dailyResetClearsAtTheNextBoundary() {
    // Day 1 at 12:00 UTC; reset hour is 0 (midnight).
    var noon = MILLIS_PER_DAY + 12 * 3_600_000L;
    var clock = new AtomicLong(noon);
    var service = service(new FakeUsage(), clock);
    var kit = dailyKit();

    service.markClaimed(PLAYER, kit);
    assertEquals(43_200, service.remainingSeconds(PLAYER, kit)); // 12h to next midnight

    clock.set(2 * MILLIS_PER_DAY - 1_000L); // one second before midnight
    assertEquals(1, service.remainingSeconds(PLAYER, kit));

    clock.set(2 * MILLIS_PER_DAY); // next midnight — new period
    assertEquals(0, service.remainingSeconds(PLAYER, kit));
  }

  private static KitCooldownService service(KitUsageRepository usage, AtomicLong clock) {
    return new KitCooldownService(usage, clock::get, () -> 0, ZoneOffset.UTC);
  }

  private static Kit rollingKit(long cooldownSeconds) {
    return kit(cooldownSeconds, false);
  }

  private static Kit dailyKit() {
    return kit(0, true);
  }

  private static Kit kit(long cooldownSeconds, boolean dailyReset) {
    return new Kit(
        "starter",
        "Starter",
        Material.CHEST,
        "default",
        cooldownSeconds,
        false,
        "",
        false,
        List.of(),
        Arrays.asList(null, null, null, null),
        null,
        dailyReset);
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
