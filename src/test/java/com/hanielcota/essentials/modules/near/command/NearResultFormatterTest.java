package com.hanielcota.essentials.modules.near.command;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.near.config.NearConfig;
import com.hanielcota.essentials.modules.near.service.NearService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class NearResultFormatterTest {

  @Test
  void joinSingleEntry() {
    var formatter = new NearResultFormatter();
    var snap = NearConfig.defaults();
    var nearby = List.of(new NearService.Nearby(UUID.randomUUID(), "Steve", 10));

    var result = formatter.join(snap, nearby);

    assertTrue(result.contains("Steve"));
    assertTrue(result.contains("10"));
  }

  @Test
  void joinMultipleEntriesWithSeparator() {
    var formatter = new NearResultFormatter();
    var snap = NearConfig.defaults();
    var nearby =
        List.of(
            new NearService.Nearby(UUID.randomUUID(), "Alice", 5),
            new NearService.Nearby(UUID.randomUUID(), "Bob", 15));

    var result = formatter.join(snap, nearby);

    assertTrue(result.contains("Alice"));
    assertTrue(result.contains("Bob"));
    assertTrue(result.contains(snap.separator()));
  }

  @Test
  void joinEmptyListReturnsEmptyString() {
    var formatter = new NearResultFormatter();
    var snap = NearConfig.defaults();

    var result = formatter.join(snap, List.of());

    assertTrue(result.isEmpty());
  }
}
