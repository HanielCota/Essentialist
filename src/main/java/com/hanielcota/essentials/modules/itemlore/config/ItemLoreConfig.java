package com.hanielcota.essentials.modules.itemlore.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record ItemLoreConfig(
    @Comment("Shown after a lore line is appended.") String added,
    @Comment("Shown after a lore line is replaced.") String updated,
    @Comment("Shown after a lore line is removed.") String removed,
    @Comment("Shown after the whole lore is cleared.") String cleared,
    @Comment("Shown when the player is not holding an item.") String emptyHand,
    @Comment("Shown when the given line number does not exist.") String invalidLine,
    @Comment("Shown when the held item has no lore to edit.") String emptyLore,
    @Comment("Usage shown when /itemlore is run without a sub-command.") String usage) {

  public static ItemLoreConfig defaults() {
    return new ItemLoreConfig(
        "<green>Lore line added.",
        "<green>Lore line updated.",
        "<yellow>Lore line removed.",
        "<yellow>Lore cleared.",
        "<red>You are not holding any item.",
        "<red>That lore line does not exist.",
        "<red>The held item has no lore.",
        "<yellow>Usage: <gray>/itemlore add <text> | set <line> <text> | remove <line> | clear");
  }
}
