package com.hanielcota.essentials.modules.repair.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.repair.config.RepairConfig;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public record RepairService(ConfigHandle<RepairConfig> config) {

  private static boolean repair(ItemStack item, @NonNull List<Material> blacklist) {
    if (item == null) {
      return false;
    }

    var type = item.getType();
    var maxDurability = type.getMaxDurability();
    if (maxDurability <= 0) {
      return false;
    }
    if (blacklist.contains(type)) {
      return false;
    }

    var meta = item.getItemMeta();
    if (!(meta instanceof Damageable damageable)) {
      return false;
    }
    if (damageable.isUnbreakable() || !damageable.hasDamage()) {
      return false;
    }

    damageable.setDamage(0);
    item.setItemMeta(damageable);
    return true;
  }

  public HandResult repairHand(@NonNull Player player) {
    var inv = player.getInventory();
    var held = inv.getItemInMainHand();
    var heldType = held.getType();

    if (heldType.isAir()) {
      return HandResult.EMPTY_HAND;
    }

    var snap = this.config.value();
    var blacklist = snap.blacklist();
    if (!repair(held, blacklist)) {
      return HandResult.NOTHING_TO_REPAIR;
    }

    inv.setItemInMainHand(held);
    return HandResult.REPAIRED;
  }

  public int repairAll(@NonNull Player player) {
    var snap = this.config.value();
    var blacklist = snap.blacklist();
    var limit = snap.repairAllLimit();
    var inv = player.getInventory();
    var size = inv.getSize();

    var count = 0;
    for (var slot = 0; slot < size && count < limit; slot++) {
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
