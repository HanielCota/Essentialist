package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import com.hanielcota.essentials.util.Numbers;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Visual + layout settings for /homes and the material picker submenu. */
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
    @Comment("/homes item lore. Placeholders: {world}, {x}, {y}, {z}.") List<String> itemLore,
    @Comment("Add an enchant glow to the /homes items.") boolean itemGlow,
    @Comment("Material category submenu title.") String categoryTitle,
    @Comment("Material category submenu rows (1-6).") int categoryRows,
    @Comment("Material category content slots (0-based).") List<Integer> categoryContentSlots,
    @Comment("Material category item name. Placeholder: {category}.") String categoryItemName,
    @Comment("Material category item lore. Placeholder: {category}.") List<String> categoryItemLore,
    @Comment("Display name of each material category shown in the picker submenu.")
        Map<MaterialCategory, String> categoryNames,
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

  private static final int MIN_ROWS = 1;

  public static HomesMenuConfig defaults() {
    return new HomesMenuConfig(
        "<dark_gray>Suas homes",
        6,
        List.of(
            11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 27, 28, 29, 30, 31, 32, 33, 34,
            36, 37, 38, 39, 40, 41, 42, 43),
        NavigationButtonsConfig.defaults(48, 50),
        10,
        Material.BOOK,
        "<yellow>Como funcionam as homes",
        List.of(
            "<gray>Pontos de teleporte pessoais.",
            "",
            "<yellow>/sethome <nome> <gray>cria uma home",
            "<yellow>/home <nome> <gray>teleporta até ela",
            "<yellow>/homes <gray>abre este menu",
            "",
            "<gray>Aqui no menu:",
            "<yellow>Clique esquerdo <gray>teleporta",
            "<yellow>Clique direito <gray>deleta",
            "<yellow>Shift + clique <gray>renomeia",
            "<yellow>Q (drop) <gray>troca o ícone",
            "",
            "<dark_gray>Seu limite depende da sua permissão."),
        "<gold>{name}",
        List.of(
            "<gray>Mundo: <white>{world}",
            "<gray>Coordenadas: <white>{x}, {y}, {z}",
            "",
            "<yellow>Clique esquerdo <gray>para teleportar",
            "<yellow>Clique direito <gray>para deletar",
            "<yellow>Shift + clique <gray>para renomear",
            "<yellow>Q (drop) <gray>para trocar o ícone"),
        false,
        "<dark_gray>Escolha uma categoria",
        4,
        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25),
        "<gold>{category}",
        List.of("<gray>Clique para ver os itens"),
        defaultCategoryNames(),
        31,
        Material.ARROW,
        "<yellow>Voltar às homes",
        List.of(),
        "<dark_gray>Escolha o ícone",
        6,
        List.of(
            10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
            38, 39, 40, 41, 42, 43),
        "<gold>{material}",
        List.of("<gray>Clique para usar <white>{material}", "<gray>Clique para escolher"),
        NavigationButtonsConfig.defaults(45, 53),
        49,
        Material.BARRIER,
        "<red>Voltar às categorias",
        List.of(),
        3,
        13,
        Material.PAPER,
        11,
        Material.LIME_WOOL,
        15,
        Material.RED_WOOL);
  }

  public String formatItemName(@NonNull String name) {
    return itemName.replace("{name}", name);
  }

  public String[] renderItemLore(@NonNull String world, double x, double y, double z) {
    var xStr = Numbers.compact(x);
    var yStr = Numbers.compact(y);
    var zStr = Numbers.compact(z);
    var rendered = new String[itemLore.size()];

    for (var i = 0; i < itemLore.size(); i++) {
      var line = itemLore.get(i);
      var withWorld = line.replace("{world}", world);
      var withX = withWorld.replace("{x}", xStr);
      var withY = withX.replace("{y}", yStr);
      rendered[i] = withY.replace("{z}", zStr);
    }
    return rendered;
  }

  public String staticPickerTitle() {
    if (pickerTitle.contains("{name}")) {
      return "<dark_gray>Escolha o ícone";
    }
    return pickerTitle;
  }

  public int effectiveRows() {
    return MenuLayouts.clampRows(rows);
  }

  public int effectiveCategoryRows() {
    return MenuLayouts.clampRows(categoryRows);
  }

  public int effectivePickerRows() {
    return MenuLayouts.clampRows(pickerRows);
  }

  public int effectiveDeleteRows() {
    return MenuLayouts.clampRows(deleteRows);
  }

  public List<Integer> effectiveContentSlots() {
    if (contentSlots.isEmpty()) {
      var effRows = effectiveRows();
      var count = effRows > MIN_ROWS ? (effRows - 1) * 9 : 9;
      return MenuLayouts.fallbackContentSlots(effRows, count);
    }
    return MenuLayouts.sanitizeSlots(contentSlots, effectiveRows());
  }

  public int effectiveInfoSlot() {
    return MenuLayouts.sanitizeSlot(infoSlot, effectiveRows(), 10);
  }

  public List<Integer> effectiveCategoryContentSlots() {
    return MenuLayouts.sanitizeSlots(categoryContentSlots, effectiveCategoryRows());
  }

  public int effectiveCategoryBackSlot() {
    return MenuLayouts.sanitizeSlot(
        categoryBackSlot, effectiveCategoryRows(), effectiveCategoryRows() * 9 - 5);
  }

  public List<Integer> effectivePickerContentSlots() {
    return MenuLayouts.sanitizeSlots(pickerContentSlots, effectivePickerRows());
  }

  public int effectivePickerBackSlot() {
    return MenuLayouts.sanitizeSlot(
        pickerBackSlot, effectivePickerRows(), effectivePickerRows() * 9 - 5);
  }

  public int effectiveDeletePromptSlot() {
    return MenuLayouts.sanitizeSlot(deletePromptSlot, effectiveDeleteRows(), 13);
  }

  public int effectiveDeleteYesSlot() {
    return MenuLayouts.sanitizeSlot(deleteYesSlot, effectiveDeleteRows(), 11);
  }

  public int effectiveDeleteNoSlot() {
    return MenuLayouts.sanitizeSlot(deleteNoSlot, effectiveDeleteRows(), 15);
  }

  public String categoryName(@NonNull MaterialCategory category) {
    var configured = categoryNames.get(category);
    return configured != null ? configured : category.name();
  }

  public String formatCategoryItemName(@NonNull String category) {
    return categoryItemName.replace("{category}", category);
  }

  public String[] formatCategoryItemLore(@NonNull String category) {
    var rendered = new String[categoryItemLore.size()];
    for (var i = 0; i < categoryItemLore.size(); i++) {
      rendered[i] = categoryItemLore.get(i).replace("{category}", category);
    }
    return rendered;
  }

  public String formatPickerItemName(@NonNull String material) {
    return pickerItemName.replace("{material}", material);
  }

  public String[] formatPickerItemLore(@NonNull String material) {
    var rendered = new String[pickerItemLore.size()];
    for (var i = 0; i < pickerItemLore.size(); i++) {
      rendered[i] = pickerItemLore.get(i).replace("{material}", material);
    }
    return rendered;
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
}
