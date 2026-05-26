package com.hanielcota.essentials.modules.chat.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.chat.config.ChatConfig;
import java.util.concurrent.atomic.AtomicReference;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

/**
 * Renders a chat line into a Component using the format template from {@link ChatConfig}.
 *
 * <p>The legacy-to-MiniMessage normalisation in {@link FormatNormalizer#normalize(String)} is the
 * expensive bit and is cached: a {@link Template} record holds the source string from disk and the
 * normalised string fed to MiniMessage. On each format call we compare the live config string by
 * reference / equality to the cached one; only on a mismatch (i.e. after a {@code /chat reload})
 * does normalisation run again. A torn read would just trigger one extra normalisation — harmless —
 * so a plain {@link AtomicReference} (no synchronisation, no volatile around extra state) is
 * enough.
 *
 * <p>The player's literal message is inserted via {@link Tag#inserting(Component)} so a user typing
 * {@code <click:run_command:'/op me'>x</click>} in chat shows up as plain text rather than a
 * working clickable tag. This is the format-injection defence — never parse user input as
 * MiniMessage in PR 1.
 */
@RequiredArgsConstructor
public final class ChatFormatter {

  private static final MiniMessage MINI = MiniMessage.miniMessage();

  private final ConfigHandle<ChatConfig> config;
  private final AtomicReference<Template> cache = new AtomicReference<>();

  public Component format(@NonNull Player sender, @NonNull Component message) {
    var snap = this.config.value();
    var template = templateFor(snap);

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

  private Template templateFor(@NonNull ChatConfig snap) {
    var current = this.cache.get();
    var source = snap.format();
    if (current != null && current.source().equals(source)) {
      return current;
    }

    var normalized = snap.acceptLegacyAmpersand() ? FormatNormalizer.normalize(source) : source;
    var fresh = new Template(source, normalized);
    this.cache.set(fresh);

    return fresh;
  }

  private record Template(String source, String normalized) {}
}
