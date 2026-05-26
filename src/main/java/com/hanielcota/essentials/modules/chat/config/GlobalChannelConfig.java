package com.hanielcota.essentials.modules.chat.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record GlobalChannelConfig(
    @Comment(
            "Prefix character(s) that route the message to global chat. Empty disables prefix"
                + " routing.")
        String prefix,
    @Comment(
            "Require the chat.global.use permission to route via prefix. When false, anyone using"
                + " the prefix sends globally. When true and the sender lacks permission, the"
                + " message falls through to local chat instead of being blocked.")
        boolean requirePermission,
    @Comment(
            "Cooldown in seconds between messages on this channel. 0 disables. Players with"
                + " chat.global.bypasscooldown ignore the cooldown.")
        int cooldownSeconds,
    @Comment("MiniMessage format template for the global channel.") String format) {

  public static GlobalChannelConfig defaults() {
    return new GlobalChannelConfig(
        "!",
        false,
        3,
        "<dark_aqua>[G]</dark_aqua> <gray><player></gray> <dark_gray>»</dark_gray>"
            + " <white><message></white>");
  }
}
