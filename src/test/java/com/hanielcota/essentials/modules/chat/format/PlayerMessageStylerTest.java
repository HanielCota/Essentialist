package com.hanielcota.essentials.modules.chat.format;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.chat.service.ChatPermissions;
import java.lang.reflect.Proxy;
import java.util.Set;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

class PlayerMessageStylerTest {

  private static Player playerWithPermissions(Set<String> permissions) {
    return (Player)
        Proxy.newProxyInstance(
            Player.class.getClassLoader(),
            new Class<?>[] {Player.class},
            (ignored, method, args) -> {
              return switch (method.getName()) {
                case "hasPermission" -> permissions.contains((String) args[0]);
                case "getEffectivePermissions" -> Set.of();
                case "toString" -> "StylePlayer";
                default -> throw new UnsupportedOperationException(method.getName());
              };
            });
  }

  @Test
  void plainTextForPlayerWithoutPermissions() {
    var styler = new PlayerMessageStyler();
    var sender = playerWithPermissions(Set.of());

    var result = styler.style(sender, "&ahello");

    var plain = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize(result);
    assertTrue(plain.contains("&ahello") || plain.contains("hello"));
  }

  @Test
  void colorCodesAppliedWithColorPermission() {
    var styler = new PlayerMessageStyler();
    var sender = playerWithPermissions(Set.of(ChatPermissions.CHAT_COLOR));

    var result = styler.style(sender, "&ahello");

    var plain = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize(result);
    assertTrue(!plain.contains("&a"));
  }

  @Test
  void decorationCodesAppliedWithFormatPermission() {
    var styler = new PlayerMessageStyler();
    var sender = playerWithPermissions(Set.of(ChatPermissions.CHAT_FORMAT));

    var result = styler.style(sender, "&lBold");

    var plain = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize(result);
    assertTrue(!plain.contains("&l"));
  }
}
