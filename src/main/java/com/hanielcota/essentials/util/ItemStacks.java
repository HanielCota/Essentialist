package com.hanielcota.essentials.util;

import org.bukkit.inventory.ItemStack;

public final class ItemStacks {

  private ItemStacks() {}

  public static boolean isPlain(ItemStack stack) {
    if (!stack.hasItemMeta()) {
      return true;
    }
    var meta = stack.getItemMeta();
    return !meta.hasDisplayName() && !meta.hasLore() && !meta.hasEnchants();
  }
}
