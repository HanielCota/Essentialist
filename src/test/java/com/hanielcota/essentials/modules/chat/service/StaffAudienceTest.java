package com.hanielcota.essentials.modules.chat.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class StaffAudienceTest {

  private static Player player(UUID id, boolean hasStaffReceive) {
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getUniqueId" -> id;
                case "hasPermission" -> {
                  if (ChatPermissions.STAFF_RECEIVE.equals(args[0])) {
                    yield hasStaffReceive;
                  }
                  yield false;
                }
                case "getEffectivePermissions" -> Set.of();
                case "toString" -> "StaffPlayer";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  @Test
  void senderAlwaysHearsThemself() {
    var senderId = UUID.randomUUID();
    var sender = player(senderId, false);

    assertTrue(StaffAudience.canHear(sender, senderId));
  }

  @Test
  void viewerWithoutPermissionCannotHear() {
    var senderId = UUID.randomUUID();
    var viewerId = UUID.randomUUID();
    var viewer = player(viewerId, false);

    assertFalse(StaffAudience.canHear(viewer, senderId));
  }

  @Test
  void viewerWithPermissionCanHear() {
    var senderId = UUID.randomUUID();
    var viewer = player(UUID.randomUUID(), true);

    assertTrue(StaffAudience.canHear(viewer, senderId));
  }
}
