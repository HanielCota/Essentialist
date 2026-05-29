package com.hanielcota.essentials.modules.more.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record MoreConfig(
    @Comment("Shown after the held stack is filled to its maximum size.") String filled,
    @Comment("Shown when the player is not holding an item.") String emptyHand,
    @Comment("Shown when the held stack is already at its maximum size.") String alreadyFull) {

  public static MoreConfig defaults() {
    return new MoreConfig(
        "<green>Stack filled to the maximum.",
        "<red>You are not holding any item.",
        "<red>The held stack is already full.");
  }
}
