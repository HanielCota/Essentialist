package com.hanielcota.essentials.modules.ping.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record PingConfig(
    @Comment("Shown with your own ping. Placeholder: {ping}.") String ownPing,
    @Comment("Placeholders: {player}, {ping}.") String otherPing,
    @Comment("Ping (ms) up to which the value is shown in green.") int goodMaxPing,
    @Comment("Ping (ms) up to which the value is shown in yellow; above it, red.")
        int mediumMaxPing) {

  public static PingConfig defaults() {
    return new PingConfig(
        "<gray>Your ping: {ping}.", "<gray><gold>{player}</gold>'s ping: {ping}.", 100, 250);
  }

  public MessagePair message() {
    return new MessagePair(ownPing, otherPing);
  }
}
