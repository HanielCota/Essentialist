package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HomesPickerMenuConfig(
    @Comment("Material picker submenu title. Placeholders: {name}.") String title,
    @Comment("Material picker rows (1-6).") int rows,
    @Comment("Material picker content slots (0-based).") List<Integer> contentSlots,
    @Comment("Material picker item name. Placeholder: {material}.") String itemName,
    @Comment("Material picker item lore. Placeholder: {material}.") List<String> itemLore,
    @Comment("Material picker previous/next page buttons.") NavigationButtonsConfig navigation,
    @Comment("Slot of the picker back button.") int backSlot,
    @Comment("Material of the picker back button.") Material backMaterial,
    @Comment("Name of the picker back button.") String backName,
    @Comment("Lore of the picker back button.") List<String> backLore) {

  public static HomesPickerMenuConfig defaults() {
    return new HomesPickerMenuConfig(
        "<dark_gray>Pick an icon",
        6,
        List.of(
            10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
            38, 39, 40, 41, 42, 43),
        "<gold>{material}",
        List.of("<gray>Click to use <white>{material}", "<gray>Click to choose"),
        NavigationButtonsConfig.defaults(45, 53),
        49,
        Material.BARRIER,
        "<red>Back to categories",
        List.of());
  }
}
