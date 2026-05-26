package com.hanielcota.essentials.modules.chat.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record GlobalChannelConfig(
    @Comment(
            "Prefix character(s) that route the message to global chat. Empty disables prefix"
                + " routing and only /chat-global-style commands would reach this channel (none in"
                + " PR 2).")
        String prefix,
    @Comment(
            "Require the chat.global.use permission to route via prefix. When false, anyone using"
                + " the prefix sends globally. When true and the sender lacks permission, the"
                + " message falls through to local chat instead of being blocked.")
        boolean requirePermission,
    @Comment(
            "MiniMessage format template for the global channel. Same placeholders as the local"
                + " format.")
        String format) {

  public static GlobalChannelConfig defaults() {
    return new GlobalChannelConfig(
        "!",
        false,
        "<dark_aqua>[G]</dark_aqua> <gray><player></gray> <dark_gray>»</dark_gray>"
            + " <white><message></white>");
  }
}
