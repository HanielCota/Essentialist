package com.hanielcota.essentials.modules.nick.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.nick.domain.NickEntry;
import com.hanielcota.essentials.modules.nick.repository.CachedNickRepository;
import com.hanielcota.essentials.modules.nick.repository.NickCache;
import com.hanielcota.essentials.support.NoopAsyncDatabaseWriter;
import com.hanielcota.essentials.support.NoopNickRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NickServiceTest {

  @Test
  void setUpdatesBothForwardAndReverseLookups() {
    var service = newService();
    var id = UUID.randomUUID();

    service.set(id, "Ace", "RealName");

    assertEquals("Ace", service.nickOf(id).orElseThrow().nickname());
    assertEquals(id, service.idByNick("ace").orElseThrow());
    assertEquals(id, service.idByNick("ACE").orElseThrow());
  }

  @Test
  void rewritingANickEvictsThePreviousReverseEntry() {
    var service = newService();
    var id = UUID.randomUUID();

    service.set(id, "OldName", "Real");
    service.set(id, "NewName", "Real");

    assertEquals("NewName", service.nickOf(id).orElseThrow().nickname());
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
    assertFalse(service.nickOf(id).isPresent());
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
    var cache = new NickCache();
    var first = UUID.randomUUID();
    var second = UUID.randomUUID();

    cache.loadAll(List.of(new NickEntry(first, "A", "x"), new NickEntry(second, "B", "y")));

    var repository =
        new CachedNickRepository(
            NoopNickRepository.INSTANCE, cache, NoopAsyncDatabaseWriter.INSTANCE);
    var service = new NickService(repository);

    assertEquals(first, service.idByNick("a").orElseThrow());
    assertEquals(second, service.idByNick("b").orElseThrow());
  }

  private static NickService newService() {
    var repository =
        new CachedNickRepository(
            NoopNickRepository.INSTANCE, new NickCache(), NoopAsyncDatabaseWriter.INSTANCE);
    return new NickService(repository);
  }
}
