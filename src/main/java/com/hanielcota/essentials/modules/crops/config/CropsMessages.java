package com.hanielcota.essentials.modules.crops.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record CropsMessages(
    @Comment("Send a message to the player when one of their actions is blocked.")
        boolean notifyPlayer,
    @Comment("Minimum milliseconds between feedback messages per player (anti-spam).")
        long cooldownMs,
    @Comment("Shown when breaking an unripe crop is blocked. MiniMessage format.")
        String breakBlocked,
    @Comment("Shown when trampling farmland is blocked. MiniMessage format.")
        String trampleBlocked) {

  public static CropsMessages defaults() {
    return new CropsMessages(
        true,
        3000L,
        "<red>You cannot break crops that have not fully grown yet.",
        "<red>You cannot trample the farmland.");
  }
}
