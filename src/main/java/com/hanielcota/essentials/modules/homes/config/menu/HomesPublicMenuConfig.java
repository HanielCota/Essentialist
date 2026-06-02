package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HomesPublicMenuConfig(
    @Comment("Public homes browse menu title.") String title,
    @Comment("Public homes menu rows (1-6).") int rows,
    @Comment("Slots (0-based) where public home entries are placed.") List<Integer> contentSlots,
    @Comment("Material shown when nobody is sharing a public home.") Material emptyMaterial,
    @Comment("Name of the placeholder shown when there are no public homes.") String emptyName,
    @Comment("Lore of the placeholder shown when there are no public homes.")
        List<String> emptyLore,
    @Comment("Previous/next page navigation buttons (only used when rows > 1).")
        NavigationButtonsConfig navigation) {

  public static HomesPublicMenuConfig defaults() {
    return new HomesPublicMenuConfig(
        "<dark_gray>Public homes",
        6,
        List.of(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34),
        Material.BARRIER,
        "<red>No public homes",
        List.of("<gray>Nobody is sharing a home yet."),
        NavigationButtonsConfig.defaults(48, 50));
  }
}
