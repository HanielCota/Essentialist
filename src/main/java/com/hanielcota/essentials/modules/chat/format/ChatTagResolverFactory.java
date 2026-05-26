package com.hanielcota.essentials.modules.chat.format;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.placeholder.PlaceholderResolver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

/**
 * Builds the {@link TagResolver} consumed by {@link ChatLineRenderer} for a single chat line. Owns
 * the placeholder lookup ({@code <prefix>}/{@code <suffix>} via PAPI) and the trusted-tag mapping
 * (player/world/displayname/message), nothing else.
 *
 * <p>Player messages enter through {@link Tag#inserting(Component)} so the resolver inserts them as
 * content — never re-parsed as MiniMessage. This is the long-standing format-injection defence: a
 * player typing {@code <click:run_command:'/op me'>x</click>} sees it as literal text rather than a
 * working clickable tag. Trusted styling for the typed message itself is delegated to {@link
 * PlayerMessageStyler}.
 */
@RequiredArgsConstructor
public final class ChatTagResolverFactory {

  private static final LegacyComponentSerializer LEGACY =
      LegacyComponentSerializer.legacyAmpersand();

  private final ConfigHandle<ChatConfig> config;
  private final PlaceholderResolver placeholders;

  public TagResolver build(@NonNull Player sender, @NonNull Component message) {
    var snap = this.config.value();
    var placeholderCfg = snap.placeholders();

    var senderName = sender.getName();
    var worldName = sender.getWorld().getName();
    var displayName = sender.displayName();
    var prefixComponent = resolveAffix(sender, placeholderCfg.prefixPlaceholder());
    var suffixComponent = resolveAffix(sender, placeholderCfg.suffixPlaceholder());

    return TagResolver.resolver(
        TagResolver.resolver("player", Tag.inserting(Component.text(senderName))),
        TagResolver.resolver("world", Tag.inserting(Component.text(worldName))),
        TagResolver.resolver("displayname", Tag.inserting(displayName)),
        TagResolver.resolver("prefix", Tag.inserting(prefixComponent)),
        TagResolver.resolver("suffix", Tag.inserting(suffixComponent)),
        TagResolver.resolver("message", Tag.inserting(message)));
  }

  /**
   * Looks up a prefix/suffix string through the resolver and converts any embedded legacy {@code &}
   * codes (the form most permission plugins emit) into a styled Component. Empty key or resolver
   * absence returns {@link Component#empty()} so the tag substitution is a no-op.
   */
  private Component resolveAffix(@NonNull Player sender, @NonNull String placeholderKey) {
    if (placeholderKey.isEmpty()) {
      return Component.empty();
    }
    if (!this.placeholders.isAvailable()) {
      return Component.empty();
    }

    var resolved = this.placeholders.apply(sender, placeholderKey);
    if (resolved.isEmpty() || resolved.equals(placeholderKey)) {
      return Component.empty();
    }

    return LEGACY.deserialize(resolved);
  }
}
