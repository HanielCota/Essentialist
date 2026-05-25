package com.hanielcota.essentials.modules.mute.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.mute.domain.Mute;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class MuteServiceTest {

  @Test
  void loadAllPopulatesActiveMutes() {
    var service = newService();
    var id = UUID.randomUUID();
    service.loadAll(List.of(Map.entry(id, Mute.permanent())));

    assertTrue(service.activeMute(id).isPresent());
  }

  @Test
  void expiredMuteIsEvictedOnRead() {
    var service = newService();
    var id = UUID.randomUUID();
    var past = Instant.now().minusSeconds(60);
    service.loadAll(List.of(Map.entry(id, Mute.until(past))));

    assertFalse(service.activeMute(id).isPresent());
    // Second read confirms the eviction stuck — same answer, no NPE on stale entry.
    assertFalse(service.activeMute(id).isPresent());
  }

  @Test
  void unmuteReturnsTrueOnlyWhenSomethingWasRemoved() {
    var service = newService();
    var id = UUID.randomUUID();
    service.loadAll(List.of(Map.entry(id, Mute.permanent())));

    assertTrue(service.unmute(id));
    assertFalse(service.unmute(id));
    assertFalse(service.activeMute(id).isPresent());
  }

  private static MuteService newService() {
    return new MuteService(null, new NoopWriter());
  }

  /**
   * Drops the work — tests only assert against the in-memory cache, which the service mutates
   * synchronously before delegating to the writer.
   */
  private static final class NoopWriter implements AsyncDatabaseWriter {
    @Override
    public CompletableFuture<Void> submit(@NonNull String operation, @NonNull Runnable work) {
      return CompletableFuture.completedFuture(null);
    }

    @Override
    public void close() {}
  }
}
