package com.hanielcota.essentials.modules.smelt.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.smelt.config.SmeltConfig;
import com.hanielcota.essentials.util.ItemStacks;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record SmeltService(ConfigHandle<SmeltConfig> config) {

  public SmeltService {
    Objects.requireNonNull(config, "config");
  }

  public int smelt(Player player) {
    var mappings = config.value().mappings();
    var inv = player.getInventory();
    int count = 0;

    for (int slot = 0; slot < inv.getSize(); slot++) {
      var item = inv.getItem(slot);
      if (item == null || !ItemStacks.isPlain(item)) {
        continue;
      }
      var result = mappings.get(item.getType());
      if (result != null) {
        inv.setItem(slot, new ItemStack(result, item.getAmount()));
        count += item.getAmount();
      }
    }
    return count;
  }
}
