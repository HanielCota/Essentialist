package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class MutesApiAdapterTest {

  @Test
  void isMutedFollowsActiveMute() {
    var service = new MuteService(null, new NoopWriter());
    var id = UUID.randomUUID();
    service.loadAll(List.of(Map.entry(id, Mute.permanent())));

    var adapter = new MutesApiAdapter(service);

    assertTrue(adapter.isMuted(id));
    assertFalse(adapter.isMuted(UUID.randomUUID()));
  }

  @Test
  void activeMuteExposesTheUnderlyingMuteWhenPresent() {
    var service = new MuteService(null, new NoopWriter());
    var id = UUID.randomUUID();
    var mute = Mute.permanent();
    service.loadAll(List.of(Map.entry(id, mute)));

    var adapter = new MutesApiAdapter(service);

    assertEquals(mute, adapter.activeMute(id).orElseThrow());
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
