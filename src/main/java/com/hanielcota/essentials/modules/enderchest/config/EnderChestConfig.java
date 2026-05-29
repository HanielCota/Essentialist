package com.hanielcota.essentials.modules.enderchest.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record EnderChestConfig(
    @Comment("Shown when you open your own ender chest.") String opened,
    @Comment("Shown when you open another player's ender chest. Placeholder: {player}.")
        String openedOther,
    @Comment("Shown when you lack permission to open another player's ender chest.")
        String noPermissionOther) {

  public static EnderChestConfig defaults() {
    return new EnderChestConfig(
        "<green>Opening your Ender Chest.",
        "<green>Opening <gold>{player}</gold>'s Ender Chest.",
        "<red>You cannot open other players' Ender Chests.");
  }

  public MessagePair whenOpened() {
    return new MessagePair(opened, openedOther);
  }
}
