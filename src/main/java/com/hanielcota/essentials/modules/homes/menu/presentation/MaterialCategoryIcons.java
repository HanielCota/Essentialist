package com.hanielcota.essentials.modules.homes.menu.presentation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;

/**
 * Static mapping of {@link MaterialCategory} to its representative icon. The mapping is closed and
 * lives with the enum — not a runtime registry, just a typed switch behind a stable API.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaterialCategoryIcons {

  public static @NonNull Material iconFor(@NonNull MaterialCategory category) {
    return switch (category) {
      case CONSTRUCTION -> Material.STONE_BRICKS;
      case WOOD -> Material.OAK_LOG;
      case DECORATION -> Material.WHITE_WOOL;
      case LIGHTING -> Material.LANTERN;
      case COMBAT -> Material.DIAMOND_SWORD;
      case TOOLS -> Material.IRON_PICKAXE;
      case MINERALS -> Material.DIAMOND_BLOCK;
      case REDSTONE -> Material.REDSTONE_BLOCK;
      case FOOD -> Material.GOLDEN_APPLE;
      case TRANSPORT -> Material.MINECART;
      case STORAGE -> Material.ENDER_CHEST;
      case MAGIC -> Material.ENCHANTING_TABLE;
      case NATURE -> Material.GRASS_BLOCK;
      case PLANTS -> Material.OAK_SAPLING;
      case FLOWERS -> Material.POPPY;
      case MISC -> Material.BARRIER;
    };
  }
}
