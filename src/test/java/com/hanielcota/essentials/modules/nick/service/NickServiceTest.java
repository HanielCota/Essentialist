package com.hanielcota.essentials.modules.nick.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import com.hanielcota.essentials.modules.nick.repository.NickCacheStore;
import com.hanielcota.essentials.modules.nick.repository.NickStore;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class NickServiceTest {

  @Test
  void setUpdatesBothForwardAndReverseLookups() {
    var service = newService();
    var id = UUID.randomUUID();

    service.set(id, "Ace", "RealName");

    assertEquals("Ace", service.nickFor(id).orElseThrow().nickname());
    assertEquals(id, service.idByNick("ace").orElseThrow());
    assertEquals(id, service.idByNick("ACE").orElseThrow());
  }

  @Test
  void rewritingANickEvictsThePreviousReverseEntry() {
    var service = newService();
    var id = UUID.randomUUID();

    service.set(id, "OldName", "Real");
    service.set(id, "NewName", "Real");

    assertEquals("NewName", service.nickFor(id).orElseThrow().nickname());
    assertEquals(id, service.idByNick("newname").orElseThrow());
    assertFalse(service.idByNick("oldname").isPresent());
  }

  @Test
  void resetClearsBothLookups() {
    var service = newService();
    var id = UUID.randomUUID();
    service.set(id, "Ace", "Real");

    assertTrue(service.reset(id));
    assertFalse(service.reset(id));
    assertFalse(service.nickFor(id).isPresent());
    assertFalse(service.idByNick("ace").isPresent());
  }

  @Test
  void isTakenByOtherIgnoresSelf() {
    var service = newService();
    var owner = UUID.randomUUID();
    var stranger = UUID.randomUUID();
    service.set(owner, "Ace", "Real");

    assertFalse(service.isTakenByOther("ace", owner));
    assertTrue(service.isTakenByOther("ace", stranger));
    assertFalse(service.isTakenByOther("free", stranger));
  }

  @Test
  void loadAllPopulatesBothLookups() {
    var cache = newCache();
    var first = UUID.randomUUID();
    var second = UUID.randomUUID();

    cache.loadAll(List.of(new NickEntry(first, "A", "x"), new NickEntry(second, "B", "y")));

    var service = new NickService(cache);
    assertEquals(first, service.idByNick("a").orElseThrow());
    assertEquals(second, service.idByNick("b").orElseThrow());
  }

  private static NickService newService() {
    return new NickService(newCache());
  }

  private static NickCacheStore newCache() {
    return new NickCacheStore(new NoopStore(), new NoopWriter());
  }

  private static final class NoopStore implements NickStore {
    @Override
    public List<NickEntry> list() {
      return List.of();
    }

    @Override
    public void save(@NonNull NickEntry entry) {}

    @Override
    public boolean delete(@NonNull UUID id) {
      return false;
    }
  }

  /**
   * Drops the work — tests only assert against the in-memory cache state, which the service mutates
   * synchronously before delegating to the writer. Running the work would NPE on the null store
   * passed in for these cache-only tests.
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
