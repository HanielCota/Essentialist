package com.hanielcota.essentials.modules.vanish.config;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record VanishConfig(
    VanishChatMessages messages,
    @Comment("/vanish list menu title.") String menuTitle,
    @Comment("Rows in the /vanish list menu (clamped to 1-6).") int menuRows,
    @Comment("Slots used by vanish entries. Leave empty to use every row except the last.")
        List<Integer> menuContentSlots,
    @Comment("Previous/next page buttons.") NavigationButtonsConfig navigation,
    @Comment("Slot of the static info item shown on every page.") int infoSlot,
    @Comment("Material of the static info item.") Material infoMaterial,
    @Comment("Name of the static info item.") String infoName,
    @Comment("Lore of the static info item.") List<String> infoLore,
    @Comment("Item name for each vanished player. Placeholder: {player}.") String itemName,
    @Comment("Item lore for each vanished player. Placeholders: {player}, {world}, {x}, {y}, {z}.")
        List<String> itemLore,
    @Comment("Material of the placeholder shown when no one is vanished.") Material emptyMaterial,
    @Comment("Name of the placeholder shown when no one is vanished.") String emptyName,
    @Comment("Lore of the placeholder shown when no one is vanished.") List<String> emptyLore) {

  public static VanishConfig defaults() {
    return new VanishConfig(
        VanishChatMessages.defaults(),
        "<dark_gray>Vanished players",
        6,
        List.of(
            11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 27, 28, 29, 30, 31, 32, 33, 34,
            36, 37, 38, 39, 40, 41, 42, 43),
        NavigationButtonsConfig.defaults(48, 50),
        10,
        Material.ENDER_EYE,
        "<yellow>Vanished players",
        List.of(
            "<gray>Players currently hidden from the server.",
            "",
            "<gray>Click a head to teleport to them.",
            "",
            "<dark_gray>» <gray>State resets when the server stops."),
        "<yellow>{player}",
        List.of(
            "<gray>World: <white>{world}",
            "<gray>Coordinates: <white>{x}, {y}, {z}",
            "",
            "<yellow>Click to teleport."),
        Material.BARRIER,
        "<red>No one is vanished",
        List.of(
            "<gray>Nobody is currently hidden.",
            "",
            "<yellow>/vanish [player]",
            "<dark_gray>» <gray>toggles vanish for a player."));
  }

  public int effectiveRows() {
    return MenuLayouts.clampRows(menuRows);
  }

  public int effectiveInfoSlot() {
    return MenuLayouts.sanitizeSlot(infoSlot, effectiveRows(), 10);
  }
}
