package com.hanielcota.essentials.modules.clear.service;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ClearService {

  private static int countItems(ItemStack[] items) {
    var total = 0;
    for (var item : items) {
      if (item != null && !item.getType().isAir()) {
        total += item.getAmount();
      }
    }
    return total;
  }

  // Clears storage (hotbar + main); armor and off-hand only when includeArmor is true.
  public int clear(Player player, boolean includeArmor) {
    var inv = player.getInventory();
    var storage = inv.getStorageContents();
    var armor = inv.getArmorContents();
    var offhand = inv.getItemInOffHand();

    var removed = countItems(storage);
    if (includeArmor) {
      removed += countItems(armor);
      if (!offhand.getType().isAir()) {
        removed += offhand.getAmount();
      }
    }
    if (removed == 0) {
      return 0;
    }

    inv.setStorageContents(new ItemStack[storage.length]);
    if (includeArmor) {
      inv.setArmorContents(new ItemStack[armor.length]);
      inv.setItemInOffHand(null);
    }
    player.updateInventory();
    return removed;
  }
}
