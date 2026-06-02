package com.hanielcota.essentials.modules.fly.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class FlyServiceTest {

  private static Player player(UUID id, GameMode mode) {
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getUniqueId" -> id;
                case "getGameMode" -> mode;
                case "setAllowFlight" -> null;
                case "setFlying" -> null;
                case "getEffectivePermissions" -> Set.of();
                case "toString" -> "FlyPlayer";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  @Test
  void enablesFlightForSurvivalPlayer() {
    var service = new FlyService();
    var id = UUID.randomUUID();
    var player = player(id, GameMode.SURVIVAL);

    var result = service.set(player, true);

    assertEquals(FlyService.Result.ENABLED, result);
    assertTrue(service.isEnabled(player));
  }

  @Test
  void disablesFlightForSurvivalPlayer() {
    var service = new FlyService();
    var id = UUID.randomUUID();
    var player = player(id, GameMode.SURVIVAL);

    service.set(player, true);
    var result = service.set(player, false);

    assertEquals(FlyService.Result.DISABLED, result);
    assertFalse(service.isEnabled(player));
  }

  @Test
  void returnsUnsupportedForCreative() {
    var service = new FlyService();
    var player = player(UUID.randomUUID(), GameMode.CREATIVE);

    var result = service.toggle(player);

    assertEquals(FlyService.Result.UNSUPPORTED, result);
  }

  @Test
  void returnsUnsupportedForSpectator() {
    var service = new FlyService();
    var player = player(UUID.randomUUID(), GameMode.SPECTATOR);

    var result = service.toggle(player);

    assertEquals(FlyService.Result.UNSUPPORTED, result);
  }

  @Test
  void isEnabledReturnsFalseBeforeToggle() {
    var service = new FlyService();
    var player = player(UUID.randomUUID(), GameMode.SURVIVAL);

    assertFalse(service.isEnabled(player));
  }

  @Test
  void forgetClearsState() {
    var service = new FlyService();
    var id = UUID.randomUUID();
    var player = player(id, GameMode.SURVIVAL);

    service.set(player, true);
    service.forget(id);

    assertFalse(service.isEnabled(player));
  }
}
