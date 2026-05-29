package com.hanielcota.essentials.modules.warps.config;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Full layout and texts of the /warp(s) menu. Everything here is admin-configurable. */
@ConfigSerializable
public record WarpsMenuConfig(
    @Comment("Inventory title of the warps menu.") String title,
    @Comment("Number of rows (1-6).") int rows,
    @Comment(
            "Slots (0-based) where warps are placed. Pagination flows through these. The default is"
                + " the bordered interior (slots 10-16, 19-25, 28-34, 37-43).")
        List<Integer> contentSlots,
    @Comment("Material filling the empty border slots. Use AIR to leave them empty.")
        Material filler,
    @Comment("Previous/next page buttons.") NavigationButtonsConfig navigation,
    @Comment("Slot of the info item.") int infoSlot,
    @Comment("Material of the info item.") Material infoMaterial,
    @Comment("Info item name.") String infoName,
    @Comment("Info item lore. Placeholder: {count} (warps the viewer can use).")
        List<String> infoLore,
    @Comment("Name of each warp item. Placeholder: {warp} (the per-warp display name).")
        String entryName,
    @Comment(
            "Lore of each warp item. Placeholders: {world}, {x}, {y}, {z}, {players}, {likes}."
                + " Whole-line tokens: {description} expands to the per-warp lore, {pvp} to the PVP"
                + " tag when PVP, {favorite} to the favorite tag when the viewer favorited it.")
        List<String> entryLore,
    @Comment("PVP tag line emitted by the {pvp} token.") String pvpTag,
    @Comment("Favorite tag line emitted by the {favorite} token.") String favoriteTag,
    @Comment("Filter button.") WarpsFilterConfig filter) {

  /** The interior of a 6-row chest (inside a 1-slot border): 28 slots starting at slot 10. */
  private static List<Integer> borderedInterior() {
    var slots = new ArrayList<Integer>(28);
    for (int row = 1; row <= 4; row++) {
      for (int column = 1; column <= 7; column++) {
        slots.add(row * 9 + column);
      }
    }
    return List.copyOf(slots);
  }

  public static WarpsMenuConfig defaults() {
    return new WarpsMenuConfig(
        "Warps",
        6,
        borderedInterior(),
        Material.BLACK_STAINED_GLASS_PANE,
        NavigationButtonsConfig.defaults(48, 50),
        49,
        Material.NETHER_STAR,
        "<gray>Warps",
        List.of("<gray>You have access to <white>{count} <gray>warp(s)."),
        "<yellow>{warp}",
        List.of(
            "{description}",
            "<gray>World: <white>{world}",
            "<gray>Location: <white>{x}, {y}, {z}",
            "<gray>Players here: <white>{players}",
            "<gray>Likes: <white>{likes}",
            "{pvp}",
            "{favorite}",
            "",
            "<green>Click <white>left<green> to teleport.",
            "<gray>Click <white>right<gray> for options."),
        "<red>PVP active",
        "<yellow>★ Favorited",
        WarpsFilterConfig.defaults());
  }
}
