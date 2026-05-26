package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.nick.domain.NickEntry;
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
    var cache = newCache();
    var id = UUID.randomUUID();
    var entry = new NickEntry(id, "Ace", "RealName");
    cache.loadAll(List.of(entry));

    var service = new NickService(cache);
    var adapter = new NicksApiAdapter(service);

    assertEquals(entry, adapter.nickOf(id).orElseThrow());
  }

  @Test
  void idByNickIsCaseInsensitive() {
    var cache = newCache();
    var id = UUID.randomUUID();
    cache.loadAll(List.of(new NickEntry(id, "Ace", "RealName")));

    var service = new NickService(cache);
    var adapter = new NicksApiAdapter(service);

    assertEquals(id, adapter.idByNick("ACE").orElseThrow());
    assertEquals(id, adapter.idByNick("ace").orElseThrow());
  }

  @Test
  void unknownIdYieldsEmpty() {
    var service = new NickService(newCache());
    var adapter = new NicksApiAdapter(service);

    assertTrue(adapter.nickOf(UUID.randomUUID()).isEmpty());
  }

  private static NickCache newCache() {
    return new NickCache(NoopNickRepository.INSTANCE, NoopAsyncDatabaseWriter.INSTANCE);
  }
}
