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
    @Comment(
            "Use the viewer's skin on the info item when material is PLAYER_HEAD. Renders "
                + "per-viewer (each player sees their own head).")
        boolean infoUsePlayerHead,
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
    @Comment("Material of the delete-confirmation no button.") Material deleteNoMaterial,
    @Comment("Slot of the + Nova home button shown on every /homes page.") int createSlot,
    @Comment("Material of the + Nova home button.") Material createMaterial,
    @Comment("Name of the + Nova home button.") String createName,
    @Comment("Lore of the + Nova home button.") List<String> createLore,
    @Comment(
            "Options menu title shown on right-click. Static — inventory titles can't be "
                + "personalised per viewer, so the home name appears in the info slot instead.")
        String optionsTitle,
    @Comment("Options menu rows (1-6).") int optionsRows,
    @Comment("Slot of the home info item in the options menu.") int optionsHomeSlot,
    @Comment("Slot of the teleport button.") int optionsTeleportSlot,
    @Comment("Material of the teleport button.") Material optionsTeleportMaterial,
    @Comment("Name of the teleport button. Placeholders: {name}.") String optionsTeleportName,
    @Comment("Lore of the teleport button.") List<String> optionsTeleportLore,
    @Comment("Slot of the rename button.") int optionsRenameSlot,
    @Comment("Material of the rename button.") Material optionsRenameMaterial,
    @Comment("Name of the rename button. Placeholders: {name}.") String optionsRenameName,
    @Comment("Lore of the rename button.") List<String> optionsRenameLore,
    @Comment("Slot of the change-icon button.") int optionsIconSlot,
    @Comment("Material of the change-icon button.") Material optionsIconMaterial,
    @Comment("Name of the change-icon button. Placeholders: {name}.") String optionsIconName,
    @Comment("Lore of the change-icon button.") List<String> optionsIconLore,
    @Comment("Slot of the delete button.") int optionsDeleteSlot,
    @Comment("Material of the delete button.") Material optionsDeleteMaterial,
    @Comment("Name of the delete button. Placeholders: {name}.") String optionsDeleteName,
    @Comment("Lore of the delete button.") List<String> optionsDeleteLore,
    @Comment("Slot of the back button.") int optionsBackSlot,
    @Comment("Material of the back button.") Material optionsBackMaterial,
    @Comment("Name of the back button.") String optionsBackName,
    @Comment("Lore of the back button.") List<String> optionsBackLore,
    @Comment("Slot of the pin / unpin button.") int optionsPinSlot,
    @Comment("Material of the pin button (shown when the home is not pinned).")
        Material optionsPinMaterial,
    @Comment("Name of the pin button. Placeholders: {name}.") String optionsPinName,
    @Comment("Lore of the pin button. Placeholders: {name}.") List<String> optionsPinLore,
    @Comment("Material of the unpin button (shown when the home is pinned).")
        Material optionsUnpinMaterial,
    @Comment("Name of the unpin button. Placeholders: {name}.") String optionsUnpinName,
    @Comment("Lore of the unpin button. Placeholders: {name}.") List<String> optionsUnpinLore,
    @Comment(
            "Prefix added to the home name in /homes when it is pinned. Empty to disable the "
                + "marker.")
        String pinnedNamePrefix,
    @Comment("Slot of the sort-cycle button on every /homes page.") int sortSlot,
    @Comment("Material of the sort button.") Material sortMaterial,
    @Comment("Name of the sort button. Placeholder: {state}.") String sortName,
    @Comment(
            "Lore of the sort button. Use {state} for the current label and {options} to expand"
                + " the full list of sort states with the active one marked.")
        List<String> sortLore,
    @Comment("Label used in {state} when sorting alphabetically.") String sortLabelName,
    @Comment("Label used in {state} when sorting by most teleports.") String sortLabelMostUsed,
    @Comment("Label used in {state} when sorting by most recent teleport.") String sortLabelRecent,
    @Comment("Suffix appended to the active option in the {options} expansion.")
        String sortActiveMarker,
    @Comment(
            "Suffix appended to the home lore showing usage stats. Empty to disable. "
                + "Placeholders: {count}, {last_used}, {created_at}.")
        List<String> usageLore,
    @Comment("Label shown for {last_used} when the home has never been teleported to.")
        String lastUsedNever) {

  public static HomesMenuConfig defaults() {
    return new HomesMenuConfig(
        "<dark_gray>Your homes",
        6,
        List.of(
            11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 27, 28, 29, 30, 31, 32, 33, 34,
            36, 37, 38, 39, 40, 41, 42, 43),
        NavigationButtonsConfig.defaults(48, 50),
        4,
        Material.PLAYER_HEAD,
        true,
        "<yellow>How homes work",
        List.of(
            "<gray>Personal teleport points.",
            "",
            "<yellow>/home <name> <gray>teleports you there",
            "<yellow>/homes <gray>opens this menu",
            "",
            "<gray>In this menu:",
            "<yellow>+ button <gray>creates a new home",
            "<yellow>Left-click <gray>teleports",
            "<yellow>Right-click <gray>opens the options menu",
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
            "<yellow>Right-click <gray>for options"),
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
        Material.RED_WOOL,
        16,
        Material.EMERALD,
        "<green>+ Nova home",
        List.of(
            "<gray>Cria uma home na sua",
            "<gray>posição atual.",
            "",
            "<yellow>Clique e digite o nome no chat."),
        "<dark_gray>Opções da home",
        3,
        4,
        11,
        Material.ENDER_PEARL,
        "<green>Teleportar",
        List.of("<gray>Vai até <gold>{name}</gold>."),
        12,
        Material.NAME_TAG,
        "<yellow>Renomear",
        List.of(
            "<gray>Troca o nome de <gold>{name}</gold>.",
            "",
            "<yellow>Clique e digite o novo nome no chat."),
        14,
        Material.PAINTING,
        "<aqua>Trocar ícone",
        List.of("<gray>Escolhe um novo ícone para <gold>{name}</gold>."),
        15,
        Material.BARRIER,
        "<red>Deletar",
        List.of("<gray>Remove <gold>{name}</gold> permanentemente."),
        22,
        Material.ARROW,
        "<yellow>Voltar",
        List.of("<gray>Retorna à lista de homes."),
        13,
        Material.NETHER_STAR,
        "<gold>Fixar no topo",
        List.of("<gray>Move <gold>{name}</gold> para o topo da lista."),
        Material.NETHER_STAR,
        "<gold>★ Desfixar",
        List.of("<gray>Remove o destaque de <gold>{name}</gold>."),
        "<gold>★ ",
        8,
        Material.HOPPER,
        "<gold>Ordenar: <yellow>{state}",
        List.of(
            "<gray>Ordena suas homes na lista.",
            "",
            "{options}",
            "",
            "<yellow>Clique para alternar."),
        "Alfabético",
        "Mais usadas",
        "Recentes",
        " <green>✓",
        List.of(
            "", "<dark_gray>Teleportes: <gray>{count}", "<dark_gray>Último uso: <gray>{last_used}"),
        "nunca");
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
    } catch (IllegalArgumentException _) {
      return DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }
  }

  public DateTimeFormatter createdTimeFormatter() {
    try {
      return DateTimeFormatter.ofPattern(createdTimeFormat);
    } catch (IllegalArgumentException _) {
      return DateTimeFormatter.ofPattern("HH:mm");
    }
  }
}
