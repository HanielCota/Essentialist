package com.hanielcota.essentials.modules.homes.menu.presentation;

import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;

/** Functional categories for Minecraft items that can be used as home icons. */
public enum MaterialCategory {
  CONSTRUCTION(Material.STONE_BRICKS),
  WOOD(Material.OAK_LOG),
  DECORATION(Material.WHITE_WOOL),
  LIGHTING(Material.LANTERN),
  COMBAT(Material.DIAMOND_SWORD),
  TOOLS(Material.IRON_PICKAXE),
  MINERALS(Material.DIAMOND_BLOCK),
  REDSTONE(Material.REDSTONE_BLOCK),
  FOOD(Material.GOLDEN_APPLE),
  TRANSPORT(Material.MINECART),
  STORAGE(Material.ENDER_CHEST),
  MAGIC(Material.ENCHANTING_TABLE),
  NATURE(Material.GRASS_BLOCK),
  PLANTS(Material.OAK_SAPLING),
  FLOWERS(Material.POPPY),
  MISC(Material.BARRIER);

  private final Material icon;

  MaterialCategory(@NonNull Material icon) {
    this.icon = icon;
  }

  /** Returns the category that owns this material, or {@link #MISC} if none does. */
  public static @NonNull MaterialCategory of(@NonNull Material material) {
    return MaterialCategoryCatalog.categoryOf(material);
  }

  public static @NonNull List<MaterialCategory> browsable() {
    return List.of(values());
  }

  public @NonNull List<Material> materials() {
    return MaterialCategoryCatalog.materials(this);
  }

  /** Representative icon for the category submenu. */
  public @NonNull Material icon() {
    return this.icon;
  }
}
