package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HomesCategoryMenuConfig(
    @Comment("Material category submenu title.") String title,
    @Comment("Material category submenu rows (1-6).") int rows,
    @Comment("Material category content slots (0-based).") List<Integer> contentSlots,
    @Comment("Material category item name. Placeholder: {category}.") String itemName,
    @Comment("Material category item lore. Placeholder: {category}.") List<String> itemLore,
    @Comment("Display name of each material category shown in the picker submenu.")
        Map<MaterialCategory, String> names,
    @Comment("Show the back button in the material category submenu.") boolean backEnabled,
    @Comment("Slot of the category back button.") int backSlot,
    @Comment("Material of the category back button.") Material backMaterial,
    @Comment("Name of the category back button.") String backName,
    @Comment("Lore of the category back button.") List<String> backLore) {

  public static HomesCategoryMenuConfig defaults() {
    return new HomesCategoryMenuConfig(
        "<dark_gray>Pick a category",
        4,
        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25),
        "<gold>{category}",
        List.of("<gray>Click to browse the items"),
        defaultNames(),
        true,
        31,
        Material.ARROW,
        "<yellow>Back to homes",
        List.of());
  }

  private static Map<MaterialCategory, String> defaultNames() {
    var map = new EnumMap<MaterialCategory, String>(MaterialCategory.class);
    map.put(MaterialCategory.CONSTRUCTION, "Construction");
    map.put(MaterialCategory.WOOD, "Wood");
    map.put(MaterialCategory.DECORATION, "Decoration");
    map.put(MaterialCategory.LIGHTING, "Lighting");
    map.put(MaterialCategory.COMBAT, "Combat");
    map.put(MaterialCategory.TOOLS, "Tools");
    map.put(MaterialCategory.MINERALS, "Minerals");
    map.put(MaterialCategory.REDSTONE, "Redstone");
    map.put(MaterialCategory.FOOD, "Food");
    map.put(MaterialCategory.TRANSPORT, "Transport");
    map.put(MaterialCategory.STORAGE, "Storage");
    map.put(MaterialCategory.MAGIC, "Magic");
    map.put(MaterialCategory.NATURE, "Nature");
    map.put(MaterialCategory.PLANTS, "Plants");
    map.put(MaterialCategory.FLOWERS, "Flowers");
    map.put(MaterialCategory.MISC, "Misc");
    return map;
  }
}
