package com.hanielcota.essentials.modules.chat.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Root config of the {@code chat} module.
 *
 * <p>{@link #format} is a MiniMessage template applied to every chat message. Placeholders
 * available out-of-the-box: {@code <player>} (sender username), {@code <displayname>} (sender
 * display name component), {@code <world>} (sender world name), {@code <message>} (the typed
 * message, inserted as a literal component to prevent format injection). Future PRs add {@code
 * <prefix>} / {@code <suffix>} once a permission-aware provider is wired in.
 *
 * <p>{@link #acceptLegacyAmpersand} controls whether {@code &}-prefixed legacy colour codes in the
 * format string are converted to MiniMessage tags at reload time. The conversion is done once per
 * reload — the hot path never re-scans for {@code &} codes.
 */
@ConfigSerializable
public record ChatConfig(
    @Comment(
            "MiniMessage template applied to every chat message. Placeholders: <player>,"
                + " <displayname>, <world>, <message>. The <message> tag is inserted as a literal"
                + " component so players cannot inject MiniMessage tags via their chat input.")
        String format,
    @Comment(
            "Accept legacy ampersand colour codes (&c, &l, etc.) in the format string alongside"
                + " MiniMessage tags. Conversion happens once at reload, never on the chat hot"
                + " path.")
        boolean acceptLegacyAmpersand,
    ChatMessages messages) {

  public static ChatConfig defaults() {
    return new ChatConfig(
        "<gray><player></gray> <dark_gray>»</dark_gray> <white><message></white>",
        true,
        ChatMessages.defaults());
  }
}
