package com.hanielcota.essentials.modules.clear.service;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ClearService {

  private static int countItems(ItemStack[] items) {
    int total = 0;
    for (ItemStack item : items) {

      if (item != null && !item.getType().isAir()) {
        total += item.getAmount();
      }
    }
    return total;
  }

  /**
   * Clears the player's storage inventory (hotbar + main). Worn armor and the off-hand item are
   * only touched when {@code includeArmor} is true.
   *
   * @return the total number of items removed
   */
  public int clear(Player player, boolean includeArmor) {
    var inv = player.getInventory();
    ItemStack[] storage = inv.getStorageContents();
    ItemStack[] armor = inv.getArmorContents();
    ItemStack offhand = inv.getItemInOffHand();

    int removed = countItems(storage);
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
