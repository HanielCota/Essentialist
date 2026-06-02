package com.hanielcota.essentials.modules.speed.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.speed.config.SpeedConfig;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class SpeedServiceTest {

  private static Player player(UUID id) {
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getUniqueId" -> id;
                case "setWalkSpeed" -> null;
                case "setFlySpeed" -> null;
                case "getEffectivePermissions" -> Set.of();
                case "toString" -> "SpeedPlayer";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  private static SpeedService service() {
    var snap = SpeedConfig.defaults();
    var handle =
        new ConfigHandle<SpeedConfig>() {
          @Override
          public String name() {
            return "speed";
          }

          @Override
          public SpeedConfig value() {
            return snap;
          }
        };
    return new SpeedService(handle);
  }

  @Test
  void setWalkSpeedReturnsTrueForValidValue() {
    var service = service();
    var player = player(UUID.randomUUID());

    assertTrue(service.setWalkSpeed(player, 5));
  }

  @Test
  void setWalkSpeedReturnsFalseForOutOfRange() {
    var service = service();
    var player = player(UUID.randomUUID());

    assertFalse(service.setWalkSpeed(player, 999));
  }

  @Test
  void setFlySpeedReturnsTrueForValidValue() {
    var service = service();
    var player = player(UUID.randomUUID());

    assertTrue(service.setFlySpeed(player, 3));
  }

  @Test
  void setFlySpeedReturnsFalseForNegative() {
    var service = service();
    var player = player(UUID.randomUUID());

    assertFalse(service.setFlySpeed(player, -5));
  }
}
