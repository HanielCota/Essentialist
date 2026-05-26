package com.hanielcota.essentials.modules.smelt.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.smelt.config.SmeltConfig;
import com.hanielcota.essentials.shared.ItemStacks;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record SmeltService(ConfigHandle<SmeltConfig> config) {

  public int smelt(@NonNull Player player) {
    var snap = this.config.value();
    var mappings = snap.mappings();
    var inv = player.getInventory();
    var size = inv.getSize();

    int count = 0;
    for (int slot = 0; slot < size; slot++) {
      var item = inv.getItem(slot);
      if (item == null || !ItemStacks.isPlain(item)) {
        continue;
      }

      var type = item.getType();
      var result = mappings.get(type);
      if (result == null) {
        continue;
      }

      // Clamp to the result's max stack size: a misconfigured mapping to a low-stack
      // material would otherwise produce an oversized ItemStack (dupe/visual issues).
      var current = item.getAmount();
      var max = result.getMaxStackSize();
      var amount = Math.min(current, max);
      var resultStack = new ItemStack(result, amount);

      inv.setItem(slot, resultStack);
      count += amount;
    }

    return count;
  }
}
