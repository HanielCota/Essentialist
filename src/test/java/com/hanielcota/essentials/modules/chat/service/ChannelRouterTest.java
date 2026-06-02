package com.hanielcota.essentials.modules.chat.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.channel.ChannelRouter;
import com.hanielcota.essentials.modules.chat.channel.LocalChannel;
import com.hanielcota.essentials.modules.chat.channel.StaffChannel;
import com.hanielcota.essentials.modules.chat.command.LocalChannelNotifier;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class ChannelRouterTest {

  private static Player player(UUID id) {
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "getUniqueId" -> id;
                case "getEffectivePermissions" -> Set.of();
                case "toString" -> "ChannelRouterPlayer";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  @Test
  void routesToStaffWhenToggled() {
    var staffToggle = new StaffChatToggleService();
    var id = UUID.randomUUID();
    staffToggle.toggle(id);

    var configHandle = configHandle();
    var notifier = new LocalChannelNotifier(configHandle);
    var local = new LocalChannel(configHandle, notifier);
    var staff = new StaffChannel();
    var router = new ChannelRouter(staffToggle, local, staff);

    var sender = player(id);
    var routed = router.route(sender, "hello");

    assertTrue(routed.channel() instanceof StaffChannel);
  }

  @Test
  void routesToLocalByDefault() {
    var staffToggle = new StaffChatToggleService();
    var configHandle = configHandle();
    var notifier = new LocalChannelNotifier(configHandle);
    var local = new LocalChannel(configHandle, notifier);
    var staff = new StaffChannel();
    var router = new ChannelRouter(staffToggle, local, staff);

    var sender = player(UUID.randomUUID());
    var routed = router.route(sender, "hello");

    assertTrue(routed.channel() instanceof LocalChannel);
  }

  private static ConfigHandle<ChatConfig> configHandle() {
    return new ConfigHandle<>() {
      @Override
      public String name() {
        return "chat";
      }

      @Override
      public ChatConfig value() {
        return ChatConfig.defaults();
      }
    };
  }
}
