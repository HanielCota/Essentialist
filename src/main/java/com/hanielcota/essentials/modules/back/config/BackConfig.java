package com.hanielcota.essentials.modules.back.config;

import com.hanielcota.essentials.util.Numbers;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record BackConfig(
    @Comment("/back menu title.") String menuTitle,
    @Comment("/back menu rows (1-6). Capacity is 5 entries.") int menuRows,
    @Comment("/back item material (Bukkit Material name).") Material itemMaterial,
    @Comment("/back item enchanted glow.") boolean itemGlow,
    @Comment("/back item name. Placeholders: {index}.") String itemName,
    @Comment("/back item lore line. Placeholders: {world}, {x}, {y}, {z}.") String itemLoreLocation,
    @Comment("/back item lore instruction line.") String itemLoreClick,
    @Comment("/back success on click. Placeholders: {world}, {x}, {y}, {z}.") String back,
    @Comment("/back failure when there is no previous location.") String noBack) {

  public static BackConfig defaults() {
    return new BackConfig(
        "Back history",
        1,
        Material.COMPASS,
        false,
        "<gold>Back #{index}",
        "<gray>World: <white>{world}",
        "<dark_gray>Click to teleport.",
        "<green>Returned to <gold>{world} {x}, {y}, {z}</gold>.",
        "<red>No previous location to return to.");
  }

  public String formatItemName(int humanIndex) {
    return itemName.replace("{index}", Integer.toString(humanIndex));
  }

  public String formatItemLoreLocation(String world, double x, double y, double z) {
    return itemLoreLocation
        .replace("{world}", world)
        .replace("{x}", Numbers.compact(x))
        .replace("{y}", Numbers.compact(y))
        .replace("{z}", Numbers.compact(z));
  }

  public String formatBack(String world, double x, double y, double z) {
    return back.replace("{world}", world)
        .replace("{x}", Numbers.compact(x))
        .replace("{y}", Numbers.compact(y))
        .replace("{z}", Numbers.compact(z));
  }
}
