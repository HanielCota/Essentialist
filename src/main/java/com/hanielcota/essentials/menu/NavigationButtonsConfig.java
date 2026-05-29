package com.hanielcota.essentials.menu;

import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Reusable visual + slot settings for paginated menus' previous/next page buttons. Embed as a
 * sub-record in any module's menu config; resolve effective slots with {@link
 * #effectivePreviousSlot(int)} / {@link #effectiveNextSlot(int)} so an out-of-range value falls
 * back to a slot in the last row.
 */
@ConfigSerializable
public record NavigationButtonsConfig(
    @Comment("Material of the previous/next page buttons.") Material material,
    @Comment("Slot of the previous-page button (0-based).") int previousSlot,
    @Comment("Slot of the next-page button (0-based).") int nextSlot,
    @Comment("Name of the previous-page button.") String previousName,
    @Comment("Name of the next-page button.") String nextName) {

  public static NavigationButtonsConfig defaults(int previousSlot, int nextSlot) {
    return new NavigationButtonsConfig(
        Material.ARROW, previousSlot, nextSlot, "<yellow> Previous page", "<yellow>Next page ");
  }

  public int effectivePreviousSlot(int rows) {
    return MenuLayouts.sanitizeSlot(previousSlot, rows, (rows - 1) * 9 + 3);
  }

  public int effectiveNextSlot(int rows) {
    return MenuLayouts.sanitizeSlot(nextSlot, rows, (rows - 1) * 9 + 5);
  }
}
