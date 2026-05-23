package com.hanielcota.essentials.modules.homes.menu.presentation;

import java.util.Locale;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;

/** Display helpers for {@link Material} — kept tiny and pure so the menu code can stay lean. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MaterialNames {

  static String pretty(Material material) {
    return material.name().toLowerCase(Locale.ROOT).replace('_', ' ');
  }
}
