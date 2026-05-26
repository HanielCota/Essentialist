package com.hanielcota.essentials.modules.mute.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.modules.mute.repository.MuteStore;
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
    var cache = newCache();
    var id = UUID.randomUUID();
    cache.loadAll(List.of(Map.entry(id, Mute.permanent())));

    var service = new MuteService(cache);
    assertTrue(service.activeMute(id).isPresent());
  }

  @Test
  void expiredMuteIsEvictedOnRead() {
    var cache = newCache();
    var id = UUID.randomUUID();
    var past = Instant.now().minusSeconds(60);
    cache.loadAll(List.of(Map.entry(id, Mute.until(past))));

    var service = new MuteService(cache);
    assertFalse(service.activeMute(id).isPresent());
    assertFalse(service.activeMute(id).isPresent());
  }

  @Test
  void unmuteReturnsTrueOnlyWhenSomethingWasRemoved() {
    var cache = newCache();
    var id = UUID.randomUUID();
    cache.loadAll(List.of(Map.entry(id, Mute.permanent())));

    var service = new MuteService(cache);
    assertTrue(service.unmute(id));
    assertFalse(service.unmute(id));
    assertFalse(service.activeMute(id).isPresent());
  }

  private static MuteCache newCache() {
    return new MuteCache(new NoopStore(), new NoopWriter());
  }

  private static final class NoopStore implements MuteStore {
    @Override
    public List<Map.Entry<UUID, Mute>> listActive(@NonNull Instant now) {
      return List.of();
    }

    @Override
    public void save(@NonNull UUID id, @NonNull Mute mute) {}

    @Override
    public boolean delete(@NonNull UUID id) {
      return false;
    }

    @Override
    public int deleteExpired(@NonNull Instant now) {
      return 0;
    }
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
