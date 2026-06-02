package com.hanielcota.essentials.modules.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class CooldownServiceTest {

  @Test
  void remainingMillisReturnsZeroBeforeAnyTouch() {
    var service = new CooldownService();
    var id = UUID.randomUUID();

    assertEquals(0L, service.remainingMillis(id, "global", 5));
  }

  @Test
  void remainingMillisReturnsZeroWhenCooldownIsConfiguredAsZero() {
    var service = new CooldownService();
    var id = UUID.randomUUID();

    assertEquals(0L, service.remainingMillis(id, "global", 0));
  }

  @Test
  void remainingMillisReturnsZeroWhenCooldownIsNegative() {
    var service = new CooldownService();
    var id = UUID.randomUUID();

    service.touch(id, "global");

    assertEquals(0L, service.remainingMillis(id, "global", -1));
  }

  @Test
  void remainingMillisReturnsPositiveWhenNotElapsed() {
    var service = new CooldownService();
    var id = UUID.randomUUID();

    service.touch(id, "global");

    var remaining = service.remainingMillis(id, "global", 999);
    assertTrue(remaining > 0L);
  }

  @Test
  void remainingMillisReturnsZeroForDifferentChannel() {
    var service = new CooldownService();
    var id = UUID.randomUUID();

    service.touch(id, "global");

    assertEquals(0L, service.remainingMillis(id, "local", 5));
  }

  @Test
  void remainingMillisReturnsZeroForDifferentPlayer() {
    var service = new CooldownService();
    var alice = UUID.randomUUID();
    var bob = UUID.randomUUID();

    service.touch(alice, "global");

    assertEquals(0L, service.remainingMillis(bob, "global", 5));
  }

  @Test
  void touchResetsCooldown() {
    var service = new CooldownService();
    var id = UUID.randomUUID();

    service.touch(id, "global");

    var first = service.remainingMillis(id, "global", 999);
    service.touch(id, "global");
    var second = service.remainingMillis(id, "global", 999);

    assertTrue(second >= first);
  }

  @Test
  void clearRemovesAllChannels() {
    var service = new CooldownService();
    var id = UUID.randomUUID();

    service.touch(id, "global");
    service.touch(id, "local");
    service.clear(id);

    assertEquals(0L, service.remainingMillis(id, "global", 5));
    assertEquals(0L, service.remainingMillis(id, "local", 5));
  }
}
