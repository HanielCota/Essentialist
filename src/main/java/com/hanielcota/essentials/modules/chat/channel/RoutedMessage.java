package com.hanielcota.essentials.modules.chat.channel;

import lombok.NonNull;

/**
 * Output of {@link ChannelRouter#route(org.bukkit.entity.Player, String)} — the channel that
 * accepted the message and the message body after any prefix stripping (e.g. {@code !hello} becomes
 * {@code hello} when global accepted it).
 */
public record RoutedMessage(@NonNull ChatChannel channel, @NonNull String message) {}
