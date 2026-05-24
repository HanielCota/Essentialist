package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.util.Numbers;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Visual + layout settings for /homes and the material picker submenu. */
@ConfigSerializable
public record HomesMenuConfig(
    @Comment("/homes menu title (MiniMessage).") String title,
    @Comment("/homes menu rows (1-6).") int rows,
    @Comment("/homes content slots (0-based). Leave empty to use every slot.")
        List<Integer> contentSlots,
    @Comment("/homes item name. Placeholders: {name}.") String itemName,
    @Comment(
            "/homes item lore. Placeholders: {world}, {x}, {y}, {z}. "
                + "Tip: hint the click actions here.")
        List<String> itemLore,
    @Comment("Add an enchant glow to the /homes items.") boolean itemGlow,
    @Comment("Material category submenu title.") String categoryTitle,
    @Comment("Material category submenu rows (1-6).") int categoryRows,
    @Comment("Material category content slots (0-based).") List<Integer> categoryContentSlots,
    @Comment("Material category item name. Placeholder: {category}.") String categoryItemName,
    @Comment("Material category item lore. Placeholder: {category}.") List<String> categoryItemLore,
    @Comment("Material picker submenu title. Placeholders: {name}.") String pickerTitle,
    @Comment("Material picker rows (1-6).") int pickerRows,
    @Comment("Material picker content slots (0-based).") List<Integer> pickerContentSlots,
    @Comment("Material picker item name. Placeholder: {material}.") String pickerItemName,
    @Comment("Material picker item lore. Placeholder: {material}.") List<String> pickerItemLore,
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
        "<dark_gray>Suas homes",
        6,
        List.of(),
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
        6,
        List.of(
            10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
            38, 39, 40, 41, 42, 43),
        "<gold>{category}",
        List.of("<gray>Clique para ver os itens"),
        "<dark_gray>Escolha o ícone",
        6,
        List.of(
            10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
            38, 39, 40, 41, 42, 43),
        "<gold>{material}",
        List.of("<gray>Clique para usar <white>{material}", "<gray>Clique para escolher"),
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
    return contentSlots.isEmpty()
        ? MenuLayouts.allSlots(effectiveRows())
        : MenuLayouts.sanitizeSlots(contentSlots, effectiveRows());
  }

  public List<Integer> effectiveCategoryContentSlots() {
    return MenuLayouts.sanitizeSlots(categoryContentSlots, effectiveCategoryRows());
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
}
