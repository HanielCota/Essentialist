package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Visual + layout settings for /homes and the material picker submenu.
 *
 * <p>Pure data carrier. Effective row/slot computation and item-formatting helpers live in {@link
 * HomesMainMenuSection}, {@link MaterialCategorySection}, {@link MaterialPickerSection} and {@link
 * DeleteDialogSection}.
 */
@ConfigSerializable
public record HomesMenuConfig(
    @Comment("/homes menu title (MiniMessage).") String title,
    @Comment("/homes menu rows (1-6).") int rows,
    @Comment("/homes content slots (0-based). Leave empty to use every slot except the last row.")
        List<Integer> contentSlots,
    @Comment("/homes previous/next page buttons.") NavigationButtonsConfig navigation,
    @Comment("Slot of the static info item shown on every /homes page.") int infoSlot,
    @Comment("Material of the static info item.") Material infoMaterial,
    @Comment("Name of the static info item.") String infoName,
    @Comment("Lore of the static info item — explain how /homes works to the player.")
        List<String> infoLore,
    @Comment("/homes item name. Placeholders: {name}.") String itemName,
    @Comment(
            "/homes item lore. Placeholders: {world}, {x}, {y}, {z}, {direction}, "
                + "{created_date}, {created_time}, {created_at}.")
        List<String> itemLore,
    @Comment("Add an enchant glow to the /homes items.") boolean itemGlow,
    @Comment("Date pattern for {created_date}, see java.time.format.DateTimeFormatter.")
        String createdDateFormat,
    @Comment("Time pattern for {created_time}, see java.time.format.DateTimeFormatter.")
        String createdTimeFormat,
    @Comment("Display names for worlds in /homes. Keys are stored world names, values are labels.")
        Map<String, String> worldNames,
    @Comment("Material category submenu title.") String categoryTitle,
    @Comment("Material category submenu rows (1-6).") int categoryRows,
    @Comment("Material category content slots (0-based).") List<Integer> categoryContentSlots,
    @Comment("Material category item name. Placeholder: {category}.") String categoryItemName,
    @Comment("Material category item lore. Placeholder: {category}.") List<String> categoryItemLore,
    @Comment("Display name of each material category shown in the picker submenu.")
        Map<MaterialCategory, String> categoryNames,
    @Comment("Show the back button in the material category submenu.") boolean categoryBackEnabled,
    @Comment("Slot of the category back button.") int categoryBackSlot,
    @Comment("Material of the category back button.") Material categoryBackMaterial,
    @Comment("Name of the category back button.") String categoryBackName,
    @Comment("Lore of the category back button.") List<String> categoryBackLore,
    @Comment("Material picker submenu title. Placeholders: {name}.") String pickerTitle,
    @Comment("Material picker rows (1-6).") int pickerRows,
    @Comment("Material picker content slots (0-based).") List<Integer> pickerContentSlots,
    @Comment("Material picker item name. Placeholder: {material}.") String pickerItemName,
    @Comment("Material picker item lore. Placeholder: {material}.") List<String> pickerItemLore,
    @Comment("Material picker previous/next page buttons.")
        NavigationButtonsConfig pickerNavigation,
    @Comment("Slot of the picker back button.") int pickerBackSlot,
    @Comment("Material of the picker back button.") Material pickerBackMaterial,
    @Comment("Name of the picker back button.") String pickerBackName,
    @Comment("Lore of the picker back button.") List<String> pickerBackLore,
    @Comment("Delete-confirmation menu rows (1-6).") int deleteRows,
    @Comment("Slot of the delete-confirmation prompt item.") int deletePromptSlot,
    @Comment("Material of the delete-confirmation prompt item.") Material deletePromptMaterial,
    @Comment("Slot of the delete-confirmation yes button.") int deleteYesSlot,
    @Comment("Material of the delete-confirmation yes button.") Material deleteYesMaterial,
    @Comment("Slot of the delete-confirmation no button.") int deleteNoSlot,
    @Comment("Material of the delete-confirmation no button.") Material deleteNoMaterial) {

  public static HomesMenuConfig defaults() {
    return new HomesMenuConfig(
        "<dark_gray>Your homes",
        6,
        List.of(
            11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 27, 28, 29, 30, 31, 32, 33, 34,
            36, 37, 38, 39, 40, 41, 42, 43),
        NavigationButtonsConfig.defaults(48, 50),
        10,
        Material.BOOK,
        "<yellow>How homes work",
        List.of(
            "<gray>Personal teleport points.",
            "",
            "<yellow>/sethome <name> <gray>creates a home",
            "<yellow>/home <name> <gray>teleports you there",
            "<yellow>/homes <gray>opens this menu",
            "",
            "<gray>In this menu:",
            "<yellow>Left-click <gray>teleports",
            "<yellow>Right-click <gray>deletes",
            "<yellow>Shift + click <gray>renames",
            "<yellow>Q (drop) <gray>changes the icon",
            "",
            "<dark_gray>Your limit depends on your permission."),
        "<gold>{name}",
        List.of(
            "<gray>World: <white>{world}",
            "<gray>Coordinates: <white>{x}, {y}, {z}",
            "<gray>Direction: <white>{direction}",
            "<gray>Created: <white>{created_at}",
            "",
            "<yellow>Left-click <gray>to teleport",
            "<yellow>Right-click <gray>to delete",
            "<yellow>Shift + click <gray>to rename",
            "<yellow>Q (drop) <gray>to change the icon"),
        false,
        "dd/MM/yyyy",
        "HH:mm",
        Map.of("world", "spawn", "world_nether", "nether", "world_the_end", "end"),
        "<dark_gray>Pick a category",
        4,
        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25),
        "<gold>{category}",
        List.of("<gray>Click to browse the items"),
        defaultCategoryNames(),
        true,
        31,
        Material.ARROW,
        "<yellow>Back to homes",
        List.of(),
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
        List.of(),
        3,
        13,
        Material.PAPER,
        11,
        Material.LIME_WOOL,
        15,
        Material.RED_WOOL);
  }

  private static Map<MaterialCategory, String> defaultCategoryNames() {
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

  public DateTimeFormatter createdDateFormatter() {
    try {
      return DateTimeFormatter.ofPattern(createdDateFormat);
    } catch (IllegalArgumentException e) {
      return DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }
  }

  public DateTimeFormatter createdTimeFormatter() {
    try {
      return DateTimeFormatter.ofPattern(createdTimeFormat);
    } catch (IllegalArgumentException e) {
      return DateTimeFormatter.ofPattern("HH:mm");
    }
  }
}
