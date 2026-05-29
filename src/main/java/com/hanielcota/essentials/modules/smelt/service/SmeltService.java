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

      var current = item.getAmount();
      var max = result.getMaxStackSize();

      if (current <= max) {
        var resultStack = new ItemStack(result, current);
        inv.setItem(slot, resultStack);
        count += current;
        continue;
      }

      var resultStack = new ItemStack(result, max);
      inv.setItem(slot, resultStack);
      count += max;

      var leftover = current - max;
      var leftoverStack = new ItemStack(type, leftover);
      var leftoverSlots = inv.addItem(leftoverStack);

      if (!leftoverSlots.isEmpty()) {
        count -= leftover;
      }
    }

    return count;
  }
}
