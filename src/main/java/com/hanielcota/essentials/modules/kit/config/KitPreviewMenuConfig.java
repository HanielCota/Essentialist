package com.hanielcota.essentials.modules.kit.config;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Appearance of the read-only kit preview, with the claim and back buttons. */
@ConfigSerializable
public record KitPreviewMenuConfig(
    @Comment("Preview title (static — inventory titles cannot change per kit).") String title,
    @Comment("Preview rows (1-6).") int rows,
    @Comment("Content slots (0-based) the kit items are shown in (read-only, paginated).")
        List<Integer> contentSlots,
    @Comment("Previous/next page buttons (used when a kit has more items than content slots).")
        NavigationButtonsConfig navigation,
    @Comment("Slot of the claim button.") int claimSlot,
    @Comment("Material of the claim button.") Material claimMaterial,
    @Comment("Name of the claim button. Placeholders: {kit}.") String claimName,
    @Comment("Lore of the claim button. Placeholders: {kit}.") List<String> claimLore,
    @Comment("Slot of the back button (returns to the kit list).") int backSlot,
    @Comment("Material of the back button.") Material backMaterial,
    @Comment("Name of the back button.") String backName,
    @Comment("Lore of the back button.") List<String> backLore) {

  public static KitPreviewMenuConfig defaults() {
    return new KitPreviewMenuConfig(
        "<dark_gray>Kit preview",
        6,
        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34),
        NavigationButtonsConfig.defaults(47, 51),
        49,
        Material.LIME_DYE,
        "<green>Claim",
        List.of("<gray>Adds <gold>{kit}</gold> to your inventory."),
        45,
        Material.ARROW,
        "<yellow>Back",
        List.of("<gray>Returns to the kit list."));
  }
}
