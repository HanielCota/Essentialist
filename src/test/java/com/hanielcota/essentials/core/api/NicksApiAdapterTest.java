package com.hanielcota.essentials.core.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import com.hanielcota.essentials.modules.nick.service.NickService;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class NicksApiAdapterTest {

  @Test
  void nickOfReturnsTheCachedEntry() {
    var service = newService();
    var id = UUID.randomUUID();
    var entry = new NickEntry(id, "Ace", "RealName");
    service.loadAll(List.of(entry));

    var adapter = new NicksApiAdapter(service);

    assertEquals(entry, adapter.nickOf(id).orElseThrow());
  }

  @Test
  void idByNickIsCaseInsensitive() {
    var service = newService();
    var id = UUID.randomUUID();
    service.loadAll(List.of(new NickEntry(id, "Ace", "RealName")));

    var adapter = new NicksApiAdapter(service);

    assertEquals(id, adapter.idByNick("ACE").orElseThrow());
    assertEquals(id, adapter.idByNick("ace").orElseThrow());
  }

  @Test
  void unknownIdYieldsEmpty() {
    var service = newService();
    var adapter = new NicksApiAdapter(service);

    assertTrue(adapter.nickOf(UUID.randomUUID()).isEmpty());
  }

  private static NickService newService() {
    return new NickService(null, new NoopWriter());
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
