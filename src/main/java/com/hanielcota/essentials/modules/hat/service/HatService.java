package com.hanielcota.essentials.modules.hat.service;

import com.hanielcota.essentials.modules.hat.config.HatConfig;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class HatService {

  public Result equip(@NonNull Player player, @NonNull HatConfig snap) {
    var inv = player.getInventory();
    var held = inv.getItemInMainHand();

    if (held.getType().isAir()) {
      return Result.EMPTY_HAND;
    }
    if (!snap.isAllowed(held.getType())) {
      return Result.NOT_ALLOWED;
    }

    var previousHelmet = inv.getHelmet();
    var hasPreviousHelmet = previousHelmet != null && !previousHelmet.getType().isAir();

    // Only a single unit goes on the head, even if a whole stack is held.
    var newHelmet = held.clone();
    newHelmet.setAmount(1);

    if (held.getAmount() == 1) {
      // Plain swap: the held slot is freed for the previous helmet.
      inv.setHelmet(newHelmet);
      inv.setItemInMainHand(previousHelmet);
      return Result.EQUIPPED;
    }

    // Stacked: the rest stays in hand, so the previous helmet needs an inventory slot. Abort if
    // there's nowhere to put it — never silently drop the old helmet at the player's feet.
    if (hasPreviousHelmet && !hasRoomFor(inv, previousHelmet)) {
      return Result.INVENTORY_FULL;
    }

    inv.setHelmet(newHelmet);
    held.setAmount(held.getAmount() - 1);
    inv.setItemInMainHand(held);
    if (hasPreviousHelmet) {
      inv.addItem(previousHelmet);
    }
    return Result.EQUIPPED;
  }

  /** True when {@code item} fits in {@code inv}'s storage slots without overflow. */
  private static boolean hasRoomFor(@NonNull PlayerInventory inv, @NonNull ItemStack item) {
    var contents = inv.getStorageContents();
    var needed = item.getAmount();
    for (var slot : contents) {
      if (slot == null || slot.getType().isAir()) {
        return true;
      }
      if (slot.isSimilar(item)) {
        var capacity = slot.getMaxStackSize() - slot.getAmount();
        if (capacity >= needed) {
          return true;
        }
        needed -= capacity;
        if (needed <= 0) {
          return true;
        }
      }
    }
    return false;
  }

  public enum Result {
    EQUIPPED,
    EMPTY_HAND,
    NOT_ALLOWED,
    INVENTORY_FULL
  }
}
