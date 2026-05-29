package com.hanielcota.essentials.modules.essentials.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** The static "how it works" guide item shown in the module-control menu. */
@ConfigSerializable
public record ModulesInfoConfig(
    @Comment("Slot (0-based) of the guide item.") int slot,
    @Comment("Material of the guide item.") Material material,
    @Comment("Name of the guide item.") String name,
    @Comment("Lore of the guide item (explains how the menu works).") List<String> lore) {

  public static ModulesInfoConfig defaults() {
    return new ModulesInfoConfig(
        4,
        Material.BOOK,
        "<gold><bold>Module Panel",
        List.of(
            "<gray>Enable or disable the server modules.",
            "",
            "<yellow>➜ <gray>Click an item to <white>toggle<gray>.",
            "<green>● <gray>Green = enabled.",
            "<red>● <gray>Red = disabled.",
            "<yellow>➜ <gray>Use the <white>filter</white> to switch category.",
            "",
            "<gold>⚠ <gray>Changes apply on the <white>next restart<gray>.",
            "<dark_gray>Everything is saved in modules.yml."));
  }
}
