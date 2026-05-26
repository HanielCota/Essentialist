package com.hanielcota.essentials.modules.chat.format;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

/**
 * Final stage of the chat formatting pipeline — takes a normalised template string and a pre-built
 * {@link TagResolver} and produces the {@link Component} that Paper will send to every viewer.
 *
 * <p>Single MiniMessage instance reused for every render — MiniMessage is documented as
 * thread-safe.
 */
public final class ChatLineRenderer {

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  public Component render(@NonNull String normalizedTemplate, @NonNull TagResolver resolver) {
    return MINI.deserialize(normalizedTemplate, resolver);
  }
}
