package com.hanielcota.essentials.modules.vanish.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record VanishConfig(
    @Comment("Shown to the player when they enter vanish.") String enabled,
    @Comment("Placeholders: {player}.") String enabledOther,
    @Comment("Shown to the player when they leave vanish.") String disabled,
    @Comment("Placeholders: {player}.") String disabledOther,
    @Comment("Shown when the target player is not online.") String targetNotFound) {

  public static VanishConfig defaults() {
    return new VanishConfig(
        "<gray>You are now <green>vanished</green>.",
        "<gray>You vanished <gold>{player}</gold>.",
        "<gray>You are no longer vanished.",
        "<gray>You unvanished <gold>{player}</gold>.",
        "<red>That player is not online.");
  }

  public MessagePair toggle(boolean vanished) {
    return vanished
        ? new MessagePair(enabled, enabledOther)
        : new MessagePair(disabled, disabledOther);
  }
}
