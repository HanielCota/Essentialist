package com.hanielcota.essentials.modules.clear.service;

import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class ClearService {

  private static int countItems(@NonNull ItemStack[] items) {
    var total = 0;

    for (var item : items) {
      if (item != null && !item.getType().isAir()) {
        total += item.getAmount();
      }
    }

    return total;
  }

  // Clears storage (hotbar + main); armor and off-hand only when includeArmor is true.
  public int clear(@NonNull Player player, boolean includeArmor) {
    var inv = player.getInventory();

    var storage = inv.getStorageContents();
    var armor = inv.getArmorContents();
    var offhand = inv.getItemInOffHand();

    var removed = countItems(storage);

    if (includeArmor) {
      var armorCount = countItems(armor);
      removed += armorCount;

      var offhandType = offhand.getType();
      if (!offhandType.isAir()) {
        var offhandAmount = offhand.getAmount();
        removed += offhandAmount;
      }
    }

    if (removed == 0) {
      return 0;
    }

    var emptyStorage = new ItemStack[storage.length];
    inv.setStorageContents(emptyStorage);

    if (includeArmor) {
      var emptyArmor = new ItemStack[armor.length];
      inv.setArmorContents(emptyArmor);

      inv.setItemInOffHand(null);
    }

    player.updateInventory();
    return removed;
  }
}
