package com.hanielcota.essentials.modules.tpa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import com.hanielcota.essentials.support.NoopAsyncDatabaseWriter;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class TpaProfileServiceTest {

  @Test
  void defaultProfileReceivesBothRequestTypesAndStartsWithNoSentRequests() {
    var service = newService();
    var playerId = UUID.randomUUID();

    var profile = service.profile(playerId);

    assertTrue(profile.accepts(TeleportRequestType.TPA));
    assertTrue(profile.accepts(TeleportRequestType.TPAHERE));
    assertEquals(0, profile.sentRequests());
    assertEquals(0, profile.receivedRequests());
  }

  @Test
  void togglesTpaAndTpaHereIndependently() {
    var service = newService();
    var playerId = UUID.randomUUID();

    service.toggle(playerId, TeleportRequestType.TPA);
    var afterTpaToggle = service.profile(playerId);

    assertFalse(afterTpaToggle.accepts(TeleportRequestType.TPA));
    assertTrue(afterTpaToggle.accepts(TeleportRequestType.TPAHERE));

    service.toggle(playerId, TeleportRequestType.TPAHERE);
    var afterTpaHereToggle = service.profile(playerId);

    assertFalse(afterTpaHereToggle.accepts(TeleportRequestType.TPA));
    assertFalse(afterTpaHereToggle.accepts(TeleportRequestType.TPAHERE));
  }

  @Test
  void recordSentIncrementsTotalSentRequests() {
    var service = newService();
    var playerId = UUID.randomUUID();

    service.recordSent(playerId);
    service.recordSent(playerId);

    var profile = service.profile(playerId);
    assertEquals(2, profile.sentRequests());
    assertEquals(0, profile.receivedRequests());
  }

  @Test
  void recordReceivedIncrementsTotalReceivedRequests() {
    var service = newService();
    var playerId = UUID.randomUUID();

    service.recordReceived(playerId);
    service.recordReceived(playerId);

    var profile = service.profile(playerId);
    assertEquals(0, profile.sentRequests());
    assertEquals(2, profile.receivedRequests());
  }

  @Test
  void loadAllSeedsPersistedProfiles() {
    var service = newService();
    var playerId = UUID.randomUUID();
    var stored = TpaProfile.defaults().toggled(TeleportRequestType.TPA);

    service.loadAll(List.of(new TpaProfileService.Entry(playerId, stored)));

    assertEquals(stored, service.profile(playerId));
  }

  private static TpaProfileService newService() {
    return new TpaProfileService(null, NoopAsyncDatabaseWriter.INSTANCE);
  }
}
