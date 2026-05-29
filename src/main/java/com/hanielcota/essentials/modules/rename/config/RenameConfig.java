package com.hanielcota.essentials.modules.rename.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record RenameConfig(
    @Comment("Shown when the player is not holding an item.") String emptyHand,
    @Comment("Shown after renaming an item. Placeholder: {name}.") String renamed,
    @Comment("Shown after the item's custom name is removed.") String cleared) {

  public static RenameConfig defaults() {
    return new RenameConfig(
        "<red>You need to be holding an item.",
        "<green>Item renamed to <reset>{name}<green>.",
        "<green>The item's name has been removed.");
  }

  public String formatRenamed(@NonNull String name) {
    return renamed.replace("{name}", name);
  }
}
