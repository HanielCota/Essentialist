package com.hanielcota.essentials.modules.vanish.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record VanishMessages(
    @Comment("Shown to the player when they enter vanish.") String enabled,
    @Comment("Placeholders: {player}.") String enabledOther,
    @Comment("Shown to the player when they leave vanish.") String disabled,
    @Comment("Placeholders: {player}.") String disabledOther,
    @Comment("Shown when the target player is not online.") String targetNotFound,
    @Comment(
            "Shown after clicking a head and teleporting. Placeholders: {player}, {world}, {x},"
                + " {y}, {z}.")
        String teleported,
    @Comment("Shown when the clicked player is no longer online. Placeholder: {player}.")
        String teleportTargetGone,
    @Comment("Shown when the teleport call itself fails.") String teleportFailed) {

  public static VanishMessages defaults() {
    return new VanishMessages(
        "<gray>You are now <green>vanished</green>.",
        "<gray>You vanished <gold>{player}</gold>.",
        "<gray>You are no longer vanished.",
        "<gray>You unvanished <gold>{player}</gold>.",
        "<red>That player is not online.",
        "<green>Teleported to <gold>{player}</gold> at <gold>{world} {x}, {y}, {z}</gold>.",
        "<red><gold>{player}</gold> is no longer online.",
        "<red>Teleport failed.");
  }

  public MessagePair toggle(boolean vanished) {
    if (vanished) {
      return new MessagePair(enabled, enabledOther);
    }

    return new MessagePair(disabled, disabledOther);
  }
}
