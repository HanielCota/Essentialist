package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import com.hanielcota.essentials.modules.nick.repository.CachedNickRepository;
import com.hanielcota.essentials.modules.nick.repository.NickCache;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.support.NoopAsyncDatabaseWriter;
import com.hanielcota.essentials.support.NoopNickRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NicksApiAdapterTest {

  @Test
  void nickOfReturnsTheCachedEntry() {
    var cache = new NickCache();
    var id = UUID.randomUUID();
    var entry = new NickEntry(id, "Ace", "RealName");
    cache.loadAll(List.of(entry));

    var api = new NickService(repository(cache));

    assertEquals(entry, api.nickOf(id).orElseThrow());
  }

  @Test
  void idByNickIsCaseInsensitive() {
    var cache = new NickCache();
    var id = UUID.randomUUID();
    cache.loadAll(List.of(new NickEntry(id, "Ace", "RealName")));

    var api = new NickService(repository(cache));

    assertEquals(id, api.idByNick("ACE").orElseThrow());
    assertEquals(id, api.idByNick("ace").orElseThrow());
  }

  @Test
  void unknownIdYieldsEmpty() {
    var api = new NickService(repository(new NickCache()));

    assertTrue(api.nickOf(UUID.randomUUID()).isEmpty());
  }

  private static CachedNickRepository repository(NickCache cache) {
    return new CachedNickRepository(
        NoopNickRepository.INSTANCE, cache, NoopAsyncDatabaseWriter.INSTANCE);
  }
}
