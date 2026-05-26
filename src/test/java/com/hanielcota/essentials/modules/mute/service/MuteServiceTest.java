package com.hanielcota.essentials.modules.mute.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.modules.mute.repository.MuteCache;
import com.hanielcota.essentials.support.NoopAsyncDatabaseWriter;
import com.hanielcota.essentials.support.NoopMuteRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    return new MuteCache(NoopMuteRepository.INSTANCE, NoopAsyncDatabaseWriter.INSTANCE);
  }
}
