package com.hanielcota.essentials.modules.clear.service;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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

  public int clear(Player player) {
    PlayerInventory inv = player.getInventory();
    int removed = countItems(inv.getContents()) + countItems(inv.getArmorContents());
    if (removed == 0) {
      return 0;
    }

    inv.clear();
    player.updateInventory();
    return removed;
  }
}
