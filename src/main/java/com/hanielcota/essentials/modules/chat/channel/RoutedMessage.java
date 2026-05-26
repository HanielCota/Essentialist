package com.hanielcota.essentials.modules.chat.channel;

import lombok.NonNull;

/**
 * Output of {@link ChannelRouter#route(org.bukkit.entity.Player, String)} — the channel that
 * accepted the message together with the body that should be rendered. The router currently leaves
 * the body untouched (no prefix stripping); the record stays parameterised on both fields so a
 * future channel that needs to rewrite the body has a place to put the rewrite.
 */
public record RoutedMessage(@NonNull ChatChannel channel, @NonNull String message) {}
