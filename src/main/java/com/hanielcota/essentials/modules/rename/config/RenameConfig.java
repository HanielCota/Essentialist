package com.hanielcota.essentials.modules.rename.config;

import java.util.Objects;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record RenameConfig(
    @Comment("Shown when the player is not holding an item.") String emptyHand,
    @Comment("Shown after renaming an item. Placeholder: {name}.") String renamed,
    @Comment("Shown after the item's custom name is removed.") String cleared) {

  public static RenameConfig defaults() {
    return new RenameConfig(
        "<red>Você precisa segurar um item.",
        "<green>Item renomeado para <reset>{name}<green>.",
        "<green>O nome do item foi removido.");
  }

  public String formatRenamed(String name) {
    Objects.requireNonNull(name, "name");
    return renamed.replace("{name}", name);
  }
}
