package com.hanielcota.essentials.modules.chat.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import com.hanielcota.essentials.modules.chat.placeholder.PlaceholderApiBridge;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

/**
 * Renders a chat line into a {@link Component} from a template string.
 *
 * <p>Pipeline per call:
 *
 * <ol>
 *   <li>Look up (or compile) the legacy-to-MiniMessage normalised template — cached per template
 *       source in a {@link ConcurrentHashMap}.
 *   <li>Apply PlaceholderAPI to the normalised template via {@link PlaceholderApiBridge}. PAPI
 *       output may itself contain legacy {@code &} codes (e.g. {@code &c[Admin]} from {@code
 *       %vault_prefix%}), so we run {@link FormatNormalizer#normalize(String)} once more — it
 *       short-circuits when no {@code &} is present.
 *   <li>Resolve {@code <prefix>} / {@code <suffix>} via PAPI using the configurable placeholder
 *       keys from {@link com.hanielcota.essentials.modules.chat.config.PlaceholderConfig}. The
 *       resolved strings go through {@link LegacyComponentSerializer#legacyAmpersand} so embedded
 *       colour codes from the permission plugin survive into the final component.
 *   <li>Build the {@link TagResolver} (player, world, message, displayname, prefix, suffix) and
 *       deserialize via MiniMessage.
 * </ol>
 *
 * <p>The player's literal message is inserted via {@link Tag#inserting(Component)} so a user typing
 * {@code <click:run_command:'/op me'>x</click>} shows up as plain text rather than a working
 * clickable tag (PR 1 invariant, still enforced here — players' MiniMessage parsing is done
 * separately in {@code PlayerMessageStyler} with a strict tag whitelist).
 */
@RequiredArgsConstructor
public final class ChatFormatter {

  private static final MiniMessage MINI = MiniMessage.miniMessage();
  private static final LegacyComponentSerializer LEGACY =
      LegacyComponentSerializer.legacyAmpersand();

  private final ConfigHandle<ChatConfig> config;
  private final PlaceholderApiBridge papi;

  private final ConcurrentHashMap<String, Template> cache = new ConcurrentHashMap<>();

  public Component format(
      @NonNull Player sender, @NonNull Component message, @NonNull String templateSource) {
    var template = this.cache.computeIfAbsent(templateSource, ChatFormatter::compile);

    var snap = this.config.value();
    var placeholders = snap.placeholders();

    var papified = this.papi.apply(sender, template.normalized());
    var renormalised = papified.indexOf('&') < 0 ? papified : FormatNormalizer.normalize(papified);

    var senderName = sender.getName();
    var worldName = sender.getWorld().getName();
    var displayName = sender.displayName();
    var prefixComponent = resolveAffix(sender, placeholders.prefixPlaceholder());
    var suffixComponent = resolveAffix(sender, placeholders.suffixPlaceholder());

    var resolver =
        TagResolver.resolver(
            TagResolver.resolver("player", Tag.inserting(Component.text(senderName))),
            TagResolver.resolver("world", Tag.inserting(Component.text(worldName))),
            TagResolver.resolver("displayname", Tag.inserting(displayName)),
            TagResolver.resolver("prefix", Tag.inserting(prefixComponent)),
            TagResolver.resolver("suffix", Tag.inserting(suffixComponent)),
            TagResolver.resolver("message", Tag.inserting(message)));

    return MINI.deserialize(renormalised, resolver);
  }

  /**
   * Looks up a prefix/suffix string through PAPI and converts any embedded legacy {@code &} codes
   * (the form most permission plugins emit) into a styled Component. Empty key or PAPI absence
   * returns {@link Component#empty()} so the tag substitution is a no-op.
   */
  private Component resolveAffix(@NonNull Player sender, @NonNull String placeholderKey) {
    if (placeholderKey.isEmpty()) {
      return Component.empty();
    }
    if (!this.papi.isAvailable()) {
      return Component.empty();
    }

    var resolved = this.papi.apply(sender, placeholderKey);
    if (resolved.isEmpty() || resolved.equals(placeholderKey)) {
      return Component.empty();
    }

    return LEGACY.deserialize(resolved);
  }

  private static Template compile(@NonNull String source) {
    var normalized = FormatNormalizer.normalize(source);

    return new Template(source, normalized);
  }

  private record Template(String source, String normalized) {}
}
