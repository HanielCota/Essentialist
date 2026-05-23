package com.hanielcota.essentials.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public final class ComponentUtils {

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private ComponentUtils() {}

  public static Component mini(String input) {
    return MINI.deserialize(input);
  }

  public static Component mini(String input, TagResolver... resolvers) {
    return MINI.deserialize(input, resolvers);
  }
}
