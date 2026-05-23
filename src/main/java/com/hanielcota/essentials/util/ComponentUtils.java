package com.hanielcota.essentials.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ComponentUtils {

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  public static Component mini(@NonNull String input) {
    return MINI.deserialize(input);
  }

  public static Component mini(@NonNull String input, @NonNull TagResolver... resolvers) {
    return MINI.deserialize(input, resolvers);
  }
}
