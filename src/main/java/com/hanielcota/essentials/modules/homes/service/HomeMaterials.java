package com.hanielcota.essentials.modules.homes.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.jspecify.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HomeMaterials {

  public static final Material DEFAULT_ICON = Material.RED_BED;

  public static Material parseIcon(@Nullable String name) {
    if (name == null || name.isBlank()) {
      return DEFAULT_ICON;
    }

    return sanitizeIcon(Material.matchMaterial(name));
  }

  public static Material sanitizeIcon(@Nullable Material material) {
    if (!isUsableIcon(material)) {
      return DEFAULT_ICON;
    }

    return material;
  }

  public static boolean isUsableIcon(@Nullable Material material) {
    if (material == null) {
      return false;
    }

    try {
      return material.isItem();
    } catch (RuntimeException | LinkageError ignored) {
      return isKnownRenderableFallback(material);
    }
  }

  private static boolean isKnownRenderableFallback(@NonNull Material material) {
    return switch (material) {
      case AIR,
          CAVE_AIR,
          VOID_AIR,
          WATER,
          LAVA,
          FIRE,
          SOUL_FIRE,
          END_PORTAL,
          NETHER_PORTAL,
          END_GATEWAY ->
          false;
      default -> true;
    };
  }
}
