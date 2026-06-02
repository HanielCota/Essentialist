package com.hanielcota.essentials.modules.whitelist.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Proxy;
import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.Test;

class WhitelistServiceTest {

  @Test
  void nameOfUsesPlayerNameWhenPresent() {
    var player = offlinePlayer(UUID.randomUUID(), "Steve");

    assertEquals("Steve", WhitelistService.nameOf(player));
  }

  @Test
  void nameOfFallsBackToUuidWhenNameIsNull() {
    var id = UUID.randomUUID();
    var player = offlinePlayer(id, null);

    assertEquals(id.toString(), WhitelistService.nameOf(player));
  }

  private static OfflinePlayer offlinePlayer(UUID id, String name) {
    return (OfflinePlayer)
        Proxy.newProxyInstance(
            OfflinePlayer.class.getClassLoader(),
            new Class<?>[] {OfflinePlayer.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getUniqueId" -> id;
                case "getName" -> name;
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }
}
