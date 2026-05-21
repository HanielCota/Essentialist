package com.hanielcota.essentials.util;

import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public final class ComponentUtils {

  private static final MiniMessage MINI = MiniMessage.miniMessage();
  private static final PlainTextComponentSerializer PLAIN =
      PlainTextComponentSerializer.plainText();

  private ComponentUtils() {}

  public static Component mini(String input) {
    return MINI.deserialize(Objects.requireNonNull(input, "input"));
  }

  public static Component mini(String input, TagResolver... resolvers) {
    return MINI.deserialize(Objects.requireNonNull(input, "input"), resolvers);
  }

  public static String plain(Component component) {
    return PLAIN.serialize(Objects.requireNonNull(component, "component"));
  }

  public static String serialize(Component component) {
    return MINI.serialize(Objects.requireNonNull(component, "component"));
  }
}
