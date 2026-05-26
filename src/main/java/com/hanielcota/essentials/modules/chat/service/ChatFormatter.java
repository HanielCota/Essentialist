package com.hanielcota.essentials.modules.chat.service;

import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

/**
 * Renders a chat line into a {@link Component} from a template string.
 *
 * <p>Templates are passed in per call rather than read from config so the formatter is stateless
 * with respect to channels — each channel ({@code GlobalChannel}, {@code LocalChannel}, {@code
 * StaffChannel}) supplies its own format. The legacy-to-MiniMessage normalisation in {@link
 * FormatNormalizer} is the expensive bit and is cached per template source in a {@link
 * ConcurrentHashMap}. After a {@code /chat reload} that changes a format, the next call recomputes
 * and caches under the new source string; the stale entry is left behind. With ~3 channels and rare
 * format edits, cache size stays bounded by a small constant in practice.
 *
 * <p>The player's literal message is inserted via {@link Tag#inserting(Component)} so a user typing
 * {@code <click:run_command:'/op me'>x</click>} shows up as plain text rather than a working
 * clickable tag — defence against format injection. Player input is never parsed as MiniMessage in
 * PR 2; that arrives gated by {@code chat.color} in PR 4.
 */
public final class ChatFormatter {

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private final ConcurrentHashMap<String, Template> cache = new ConcurrentHashMap<>();

  public Component format(
      @NonNull Player sender, @NonNull Component message, @NonNull String templateSource) {
    var template = this.cache.computeIfAbsent(templateSource, ChatFormatter::compile);

    var senderName = sender.getName();
    var worldName = sender.getWorld().getName();
    var displayName = sender.displayName();

    var resolver =
        TagResolver.resolver(
            TagResolver.resolver("player", Tag.inserting(Component.text(senderName))),
            TagResolver.resolver("world", Tag.inserting(Component.text(worldName))),
            TagResolver.resolver("displayname", Tag.inserting(displayName)),
            TagResolver.resolver("message", Tag.inserting(message)));

    return MINI.deserialize(template.normalized(), resolver);
  }

  private static Template compile(@NonNull String source) {
    var normalized = FormatNormalizer.normalize(source);

    return new Template(source, normalized);
  }

  private record Template(String source, String normalized) {}
}
