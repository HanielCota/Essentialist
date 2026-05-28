package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HomesMainMenuConfig(
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
    @Comment("Slot of the + Nova home button shown on every /homes page.") int createSlot,
    @Comment("Material of the + Nova home button.") Material createMaterial,
    @Comment("Name of the + Nova home button.") String createName,
    @Comment("Lore of the + Nova home button.") List<String> createLore,
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
        String lastUsedNever,
    @Comment(
            "Prefix added to the home name in /homes when it is pinned. Empty to disable the "
                + "marker.")
        String pinnedNamePrefix) {

  public static HomesMainMenuConfig defaults() {
    return new HomesMainMenuConfig(
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
        16,
        Material.EMERALD,
        "<green>+ Nova home",
        List.of(
            "<gray>Cria uma home na sua",
            "<gray>posição atual.",
            "",
            "<yellow>Clique e digite o nome no chat."),
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
        " <green>◀",
        List.of(
            "", "<dark_gray>Teleportes: <gray>{count}", "<dark_gray>Último uso: <gray>{last_used}"),
        "nunca",
        "<gold>★ ");
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
