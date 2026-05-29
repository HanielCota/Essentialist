package com.hanielcota.essentials.modules.homes.config.menu;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HomesOptionsMenuConfig(
    @Comment(
            "Options menu title shown on right-click. Static — inventory titles can't be "
                + "personalised per viewer, so the home name appears in the info slot instead.")
        String title,
    @Comment("Options menu rows (1-6).") int rows,
    @Comment("Slot of the home info item in the options menu.") int homeSlot,
    @Comment("Slot of the teleport button.") int teleportSlot,
    @Comment("Material of the teleport button.") Material teleportMaterial,
    @Comment("Name of the teleport button. Placeholders: {name}.") String teleportName,
    @Comment("Lore of the teleport button.") List<String> teleportLore,
    @Comment("Slot of the rename button.") int renameSlot,
    @Comment("Material of the rename button.") Material renameMaterial,
    @Comment("Name of the rename button. Placeholders: {name}.") String renameName,
    @Comment("Lore of the rename button.") List<String> renameLore,
    @Comment("Slot of the change-icon button.") int iconSlot,
    @Comment("Material of the change-icon button.") Material iconMaterial,
    @Comment("Name of the change-icon button. Placeholders: {name}.") String iconName,
    @Comment("Lore of the change-icon button.") List<String> iconLore,
    @Comment("Slot of the delete button.") int deleteSlot,
    @Comment("Material of the delete button.") Material deleteMaterial,
    @Comment("Name of the delete button. Placeholders: {name}.") String deleteName,
    @Comment("Lore of the delete button.") List<String> deleteLore,
    @Comment("Slot of the back button.") int backSlot,
    @Comment("Material of the back button.") Material backMaterial,
    @Comment("Name of the back button.") String backName,
    @Comment("Lore of the back button.") List<String> backLore,
    @Comment("Slot of the pin / unpin button.") int pinSlot,
    @Comment("Material of the pin button (shown when the home is not pinned).")
        Material pinMaterial,
    @Comment("Name of the pin button. Placeholders: {name}.") String pinName,
    @Comment("Lore of the pin button. Placeholders: {name}.") List<String> pinLore,
    @Comment("Material of the unpin button (shown when the home is pinned).")
        Material unpinMaterial,
    @Comment("Name of the unpin button. Placeholders: {name}.") String unpinName,
    @Comment("Lore of the unpin button. Placeholders: {name}.") List<String> unpinLore,
    @Comment("Name shown in the info slot when the targeted home no longer exists.")
        String unavailableName) {

  // The action buttons span the first three rows, so anything below 3 would collapse them onto the
  // same slot; clamp to a usable height.
  private static final int MIN_ROWS = 3;
  private static final int MAX_ROWS = 6;

  public static HomesOptionsMenuConfig defaults() {
    return new HomesOptionsMenuConfig(
        "<dark_gray>Home options",
        3,
        4,
        11,
        Material.ENDER_PEARL,
        "<green>Teleport",
        List.of("<gray>Goes to <gold>{name}</gold>."),
        12,
        Material.NAME_TAG,
        "<yellow>Rename",
        List.of(
            "<gray>Changes the name of <gold>{name}</gold>.",
            "",
            "<yellow>Click and type the new name in chat."),
        14,
        Material.PAINTING,
        "<aqua>Change icon",
        List.of("<gray>Choose a new icon for <gold>{name}</gold>."),
        15,
        Material.BARRIER,
        "<red>Delete",
        List.of("<gray>Removes <gold>{name}</gold> permanently."),
        22,
        Material.ARROW,
        "<yellow>Back",
        List.of("<gray>Returns to the homes list."),
        13,
        Material.NETHER_STAR,
        "<gold>Pin to top",
        List.of("<gray>Moves <gold>{name}</gold> to the top of the list."),
        Material.NETHER_STAR,
        "<gold>★ Unpin",
        List.of("<gray>Removes the highlight from <gold>{name}</gold>."),
        "<red>Home unavailable");
  }

  public int effectiveRows() {
    return Math.clamp(rows, MIN_ROWS, MAX_ROWS);
  }
}
