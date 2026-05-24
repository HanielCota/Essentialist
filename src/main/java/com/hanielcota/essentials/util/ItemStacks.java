package com.hanielcota.essentials.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemStacks {

  public static boolean isPlain(@NonNull ItemStack stack) {
    if (!stack.hasItemMeta()) {
      return true;
    }

    var meta = stack.getItemMeta();
    if (meta == null) {
      return true;
    }

    var noDisplay = !meta.hasDisplayName();
    var noLore = !meta.hasLore();
    var noEnchants = !meta.hasEnchants();

    return noDisplay && noLore && noEnchants;
  }
}
