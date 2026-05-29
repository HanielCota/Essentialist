package com.hanielcota.essentials.modules.warps.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Per-warp menu customization, keyed by warp name in {@link WarpsConfig}. Every field is optional —
 * a warp with no entry (or with a field left out) falls back to its stored value: the warp's own
 * icon, its raw name, no description, and not-PVP. The location/world is never configured here; it
 * comes from {@code /setwarp}.
 */
@ConfigSerializable
public record WarpMenuEntry(
    @Comment("Menu icon Material. Overrides the warp's stored icon when set.") Material icon,
    @Comment("Display name shown in the menu (MiniMessage). Falls back to the warp name.")
        String displayName,
    @Comment("Extra description lines shown above the location info (MiniMessage).")
        List<String> lore,
    @Comment("Whether this warp counts as PVP-active for the 'PVP active' filter and lore tag.")
        boolean pvp) {

  /** A sample entry written into the default config so admins can see the shape and copy it. */
  public static WarpMenuEntry example() {
    return new WarpMenuEntry(
        Material.NETHER_STAR,
        "<yellow>Example Warp",
        List.of("<gray>Configurable description", "<gray>per warp."),
        false);
  }
}
