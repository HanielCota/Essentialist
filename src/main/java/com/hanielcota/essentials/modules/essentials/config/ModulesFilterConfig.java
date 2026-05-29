package com.hanielcota.essentials.modules.essentials.config;

import com.hanielcota.essentials.modules.essentials.menu.ModuleCategory;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Look and labels of the module-menu category filter button (the cycling control). */
@ConfigSerializable
public record ModulesFilterConfig(
    @Comment("Slot (0-based) of the filter button.") int slot,
    @Comment("Material of the filter button.") Material material,
    @Comment("Filter button name. Placeholder: {state} (the active category label).") String name,
    @Comment("Filter button lore. {options} expands to the list with the active one marked.")
        List<String> lore,
    @Comment("Marker appended to the active option in {options}.") String activeMarker,
    @Comment("Label for the All filter (shows every module).") String labelAll,
    @Comment("Label for the Protection category.") String labelProtection,
    @Comment("Label for the Teleport category.") String labelTeleport,
    @Comment("Label for the Chat category.") String labelChat,
    @Comment("Label for the Items category.") String labelItems,
    @Comment("Label for the Player category.") String labelPlayer,
    @Comment("Label for the Admin category.") String labelAdmin,
    @Comment("Label for the Other category.") String labelOther) {

  public static ModulesFilterConfig defaults() {
    return new ModulesFilterConfig(
        45,
        Material.HOPPER,
        "<yellow>Filter: <white>{state}",
        List.of("<gray>Displayed module category.", "", "{options}", "", "<green>Click to cycle."),
        " <green>◀",
        "All",
        "Protection",
        "Teleport",
        "Chat",
        "Items",
        "Player",
        "Admin",
        "Other");
  }

  public String labelOf(@NonNull ModuleCategory category) {
    return switch (category) {
      case ALL -> this.labelAll;
      case PROTECTION -> this.labelProtection;
      case TELEPORT -> this.labelTeleport;
      case CHAT -> this.labelChat;
      case ITEMS -> this.labelItems;
      case PLAYER -> this.labelPlayer;
      case ADMIN -> this.labelAdmin;
      case OTHER -> this.labelOther;
    };
  }
}
