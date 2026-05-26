package com.hanielcota.essentials.modules.tpa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.support.NoopAsyncDatabaseWriter;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TpaBlockServiceTest {

  @Test
  void blockAndUnblockControlsWhetherRequesterCanSendToBlocker() {
    var service = newService();
    var blockerId = UUID.randomUUID();
    var blockedId = UUID.randomUUID();

    service.block(blockerId, blockedId, "Alice");

    assertTrue(service.isBlocked(blockerId, blockedId));

    service.unblock(blockerId, blockedId);

    assertFalse(service.isBlocked(blockerId, blockedId));
  }

  @Test
  void listReturnsBlockedPlayersForOneBlockerOnly() {
    var service = newService();
    var blockerId = UUID.randomUUID();
    var otherBlockerId = UUID.randomUUID();
    var blockedId = UUID.randomUUID();

    service.block(blockerId, blockedId, "Alice");
    service.block(otherBlockerId, UUID.randomUUID(), "Bob");

    var blocked = service.blockedBy(blockerId);

    assertEquals(List.of(new TpaBlockService.Entry(blockerId, blockedId, "Alice")), blocked);
  }

  private static TpaBlockService newService() {
    return new TpaBlockService(null, NoopAsyncDatabaseWriter.INSTANCE);
  }
}
