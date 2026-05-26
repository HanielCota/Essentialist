package com.hanielcota.essentials.modules.chat.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Settings for the global channel. The channel is reached exclusively via the {@code /g} (alias
 * {@code /global}) command — there is no longer a prefix-routed alternative. The {@code
 * chat.global.use} permission gates the command itself; cooldown + anti-spam apply identically to
 * messages sent through the command.
 */
@ConfigSerializable
public record GlobalChannelConfig(
    @Comment(
            "Cooldown in seconds between /g messages. 0 disables. Players with"
                + " chat.global.bypasscooldown ignore the cooldown.")
        int cooldownSeconds,
    @Comment("MiniMessage format template for the global channel.") String format) {

  public static GlobalChannelConfig defaults() {
    return new GlobalChannelConfig(
        3,
        "<dark_aqua>[G]</dark_aqua> <gray><player></gray> <dark_gray>»</dark_gray>"
            + " <white><message></white>");
  }
}
