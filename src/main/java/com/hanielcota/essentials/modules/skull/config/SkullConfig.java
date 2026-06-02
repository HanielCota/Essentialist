package com.hanielcota.essentials.modules.skull.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record SkullConfig(
    @Comment("Shown when the player receives their own skull.") String receivedOwn,
    @Comment("Shown when the player receives another player's skull. Placeholders: {player}.")
        String receivedOther,
    @Comment("Shown when the target player is not found.") String playerNotFound,
    @Comment("Shown when the inventory is full.") String inventoryFull) {

  public static SkullConfig defaults() {
    return new SkullConfig(
        "<green>You received your own skull.",
        "<green>You received <gold>{player}</gold>'s skull.",
        "<red>Player not found.",
        "<red>Your inventory is full.");
  }

  public MessagePair whenReceived() {
    return new MessagePair(receivedOwn, receivedOther);
  }
}
