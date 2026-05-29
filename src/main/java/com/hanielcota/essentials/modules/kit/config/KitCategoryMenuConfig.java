package com.hanielcota.essentials.modules.kit.config;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Appearance of the kit category menu (the /kit landing menu). */
@ConfigSerializable
public record KitCategoryMenuConfig(
    @Comment("Category menu title (MiniMessage).") String title,
    @Comment("Category menu rows (1-6).") int rows,
    @Comment("Content slots (0-based) the category icons are paginated through.")
        List<Integer> contentSlots,
    @Comment("Previous/next page buttons.") NavigationButtonsConfig navigation,
    @Comment("Category item name. Placeholders: {category}, {kits}.") String itemName,
    @Comment("Category item lore. Placeholders: {category}, {kits}.") List<String> itemLore) {

  public static KitCategoryMenuConfig defaults() {
    return new KitCategoryMenuConfig(
        "<dark_gray>Kits",
        5,
        List.of(11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33),
        NavigationButtonsConfig.defaults(39, 41),
        "<gold>{category}",
        List.of("<gray>Kits: <white>{kits}", "", "<yellow>Click to browse."));
  }
}
