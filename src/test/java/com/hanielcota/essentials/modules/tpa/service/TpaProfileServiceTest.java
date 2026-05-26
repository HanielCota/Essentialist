package com.hanielcota.essentials.modules.tpa.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.tpa.domain.FavoriteOrdering;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaProfile;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
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
    var stored =
        new TpaProfile(
            false, true, 7, 3, 0, 0, 0, false, true, true, true, 0, FavoriteOrdering.NAME);

    service.loadAll(List.of(new TpaProfileService.Entry(playerId, stored)));

    assertEquals(stored, service.profile(playerId));
  }

  private static TpaProfileService newService() {
    return new TpaProfileService(null, new NoopWriter());
  }

  private static final class NoopWriter implements AsyncDatabaseWriter {
    @Override
    public CompletableFuture<Void> submit(@NonNull String operation, @NonNull Runnable work) {
      return CompletableFuture.completedFuture(null);
    }

    @Override
    public void close() {}
  }
}
