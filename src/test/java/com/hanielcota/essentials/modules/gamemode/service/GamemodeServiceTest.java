package com.hanielcota.essentials.modules.gamemode.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.UUID;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class GamemodeServiceTest {

  private static Player player(GameMode currentMode) {
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getUniqueId" -> UUID.randomUUID();
                case "getGameMode" -> currentMode;
                case "setGameMode" -> null;
                case "getEffectivePermissions" -> Set.of();
                case "toString" -> "GamemodePlayer";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  @Test
  void applyChangesModeWhenDifferent() {
    var service = new GamemodeService();
    var player = player(GameMode.SURVIVAL);

    var result = service.apply(player, GameMode.CREATIVE);

    assertEquals(GamemodeService.Result.CHANGED, result);
  }

  @Test
  void applyReturnsAlreadyInModeWhenSame() {
    var service = new GamemodeService();
    var player = player(GameMode.CREATIVE);

    var result = service.apply(player, GameMode.CREATIVE);

    assertEquals(GamemodeService.Result.ALREADY_IN_MODE, result);
  }
}
