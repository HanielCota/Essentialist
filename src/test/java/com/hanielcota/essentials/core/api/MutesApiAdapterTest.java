package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.mute.domain.Mute;
import com.hanielcota.essentials.modules.mute.repository.MuteCache;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import com.hanielcota.essentials.support.NoopAsyncDatabaseWriter;
import com.hanielcota.essentials.support.NoopMuteRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MutesApiAdapterTest {

  @Test
  void isMutedFollowsActiveMute() {
    var cache = new MuteCache(NoopMuteRepository.INSTANCE, NoopAsyncDatabaseWriter.INSTANCE);
    var id = UUID.randomUUID();
    cache.loadAll(List.of(Map.entry(id, Mute.permanent())));

    var service = new MuteService(cache);
    var adapter = new MutesApiAdapter(service);

    assertTrue(adapter.isMuted(id));
    assertFalse(adapter.isMuted(UUID.randomUUID()));
  }

  @Test
  void activeMuteExposesTheUnderlyingMuteWhenPresent() {
    var cache = new MuteCache(NoopMuteRepository.INSTANCE, NoopAsyncDatabaseWriter.INSTANCE);
    var id = UUID.randomUUID();
    var mute = Mute.permanent();
    cache.loadAll(List.of(Map.entry(id, mute)));

    var service = new MuteService(cache);
    var adapter = new MutesApiAdapter(service);

    assertEquals(mute, adapter.activeMute(id).orElseThrow());
  }
}
