package com.hanielcota.essentials.modules.homes.menu;

import java.util.Locale;
import org.bukkit.Material;

/** Display helpers for {@link Material} — kept tiny and pure so the menu code can stay lean. */
final class MaterialNames {

  private MaterialNames() {}

  static String pretty(Material material) {
    return material.name().toLowerCase(Locale.ROOT).replace('_', ' ');
  }
}
