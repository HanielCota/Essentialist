package com.hanielcota.essentials.modules.kit.config;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Appearance of the paginated kit list for a category, including the locked/cooldown states. */
@ConfigSerializable
public record KitListMenuConfig(
    @Comment("Kit list title (static — inventory titles cannot change per category).") String title,
    @Comment("Kit list rows (1-6).") int rows,
    @Comment("Content slots (0-based) the kits are paginated through.") List<Integer> contentSlots,
    @Comment("Previous/next page buttons.") NavigationButtonsConfig navigation,
    @Comment("Kit item name. Placeholders: {kit}.") String itemName,
    @Comment("Add an enchant glow to claimable kits.") boolean glowWhenAvailable,
    @Comment(
            "Material shown instead of the kit icon when the kit is locked (no permission / used).")
        Material lockedMaterial,
    @Comment("Lore appended when the kit can be claimed now.") List<String> availableLore,
    @Comment("Lore appended when the kit is on cooldown. Placeholders: {time}.")
        List<String> cooldownLore,
    @Comment("Lore appended when the player lacks the kit permission.")
        List<String> noPermissionLore,
    @Comment("Lore appended when a one-time kit was already claimed.") List<String> claimedLore,
    @Comment("Slot of the back button (returns to the category menu).") int backSlot,
    @Comment("Material of the back button.") Material backMaterial,
    @Comment("Name of the back button.") String backName,
    @Comment("Lore of the back button.") List<String> backLore) {

  public static KitListMenuConfig defaults() {
    return new KitListMenuConfig(
        "<dark_gray>Kits",
        6,
        List.of(
            10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
            38, 39, 40, 41, 42, 43),
        NavigationButtonsConfig.defaults(48, 50),
        "<gold>{kit}",
        true,
        Material.GRAY_DYE,
        List.of("", "<green>Click to preview and claim."),
        List.of("", "<red>On cooldown: <yellow>{time}</yellow>."),
        List.of("", "<red>You do not have access to this kit."),
        List.of("", "<red>Already claimed."),
        45,
        Material.ARROW,
        "<yellow>Back",
        List.of("<gray>Returns to the categories."));
  }
}
