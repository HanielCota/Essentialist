package com.hanielcota.essentials.modules.chat.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record LocalChannelConfig(
    @Comment(
            "Maximum block radius another player must be within to receive a local chat message."
                + " Distance is checked in 3D, same-world only. Players with chat.local.bypassrange"
                + " ignore this limit.")
        double radius,
    @Comment(
            "Shown to the sender when no one is within range to hear them. Set to empty to"
                + " silently drop the message.")
        String noListenerWarning,
    @Comment(
            "Cooldown in seconds between messages on this channel. 0 disables. Players with"
                + " chat.local.bypasscooldown ignore the cooldown.")
        int cooldownSeconds,
    @Comment("MiniMessage format template for the local channel.") String format) {

  public static LocalChannelConfig defaults() {
    return new LocalChannelConfig(
        100.0,
        "<gray>Ninguém te escutou aqui perto.",
        0,
        "<gray><player></gray> <dark_gray>»</dark_gray> <white><message></white>");
  }

  public double radiusSquared() {
    return radius * radius;
  }
}
