package com.hanielcota.essentials.modules.repair.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.repair.config.RepairConfig;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public record RepairService(ConfigHandle<RepairConfig> config) {

  private static boolean repair(ItemStack item, List<Material> blacklist) {
    if (item == null || item.getType().getMaxDurability() <= 0) {
      return false;
    }
    if (blacklist.contains(item.getType())) {
      return false;
    }
    if (!(item.getItemMeta() instanceof Damageable damageable)
        || damageable.isUnbreakable()
        || !damageable.hasDamage()) {
      return false;
    }

    damageable.setDamage(0);
    item.setItemMeta(damageable);
    return true;
  }

  public HandResult repairHand(Player player) {
    var inv = player.getInventory();
    var held = inv.getItemInMainHand();

    if (held.getType().isAir()) {
      return HandResult.EMPTY_HAND;
    }
    if (!repair(held, config.value().blacklist())) {
      return HandResult.NOTHING_TO_REPAIR;
    }

    inv.setItemInMainHand(held);
    return HandResult.REPAIRED;
  }

  public int repairAll(Player player) {
    var snap = config.value();
    var blacklist = snap.blacklist();
    int limit = snap.repairAllLimit();
    var inv = player.getInventory();
    int count = 0;

    for (int slot = 0; slot < inv.getSize() && count < limit; slot++) {
      var item = inv.getItem(slot);
      if (repair(item, blacklist)) {
        inv.setItem(slot, item);
        count++;
      }
    }

    return count;
  }

  public enum HandResult {
    REPAIRED,
    EMPTY_HAND,
    NOTHING_TO_REPAIR
  }
}
