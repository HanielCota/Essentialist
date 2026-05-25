package com.hanielcota.essentials.modules.invsee.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Writes the editable slots of an /invsee view back into the target's real inventory (storage,
 * armor, offhand). Locked slots are intentionally skipped — they hold filler items only. Stateless.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvseeWriteback {

  public static void apply(@NonNull Player target, @NonNull Inventory view) {
    var inv = target.getInventory();

    var storage = new ItemStack[InvseeService.STORAGE_SLOTS];
    for (var slot = 0; slot < InvseeService.STORAGE_SLOTS; slot++) {
      storage[slot] = view.getItem(slot);
    }
    inv.setStorageContents(storage);

    var helmet = view.getItem(InvseeService.HELMET_SLOT);
    var chestplate = view.getItem(InvseeService.CHESTPLATE_SLOT);
    var leggings = view.getItem(InvseeService.LEGGINGS_SLOT);
    var boots = view.getItem(InvseeService.BOOTS_SLOT);
    var offhand = view.getItem(InvseeService.OFFHAND_SLOT);

    inv.setHelmet(helmet);
    inv.setChestplate(chestplate);
    inv.setLeggings(leggings);
    inv.setBoots(boots);
    inv.setItemInOffHand(offhand);
  }
}
