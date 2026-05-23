package com.hanielcota.essentials.modules.hat.service;

import lombok.NonNull;
import org.bukkit.entity.Player;

public final class HatService {

  public Result equip(@NonNull Player player) {
    var inv = player.getInventory();
    var held = inv.getItemInMainHand();

    if (held.getType().isAir()) {
      return Result.EMPTY_HAND;
    }

    var previousHelmet = inv.getHelmet();

    // Only a single unit goes on the head, even if a whole stack is held.
    var newHelmet = held.clone();
    newHelmet.setAmount(1);
    inv.setHelmet(newHelmet);

    if (held.getAmount() == 1) {
      // Plain swap: the held slot is freed for the previous helmet.
      inv.setItemInMainHand(previousHelmet);
      return Result.EQUIPPED;
    }

    // The rest of the stack stays in hand; the old helmet returns to the inventory.
    held.setAmount(held.getAmount() - 1);
    inv.setItemInMainHand(held);

    if (previousHelmet != null && !previousHelmet.getType().isAir()) {
      var overflow = inv.addItem(previousHelmet);
      for (var drop : overflow.values()) {
        player.getWorld().dropItem(player.getLocation(), drop);
      }
    }
    return Result.EQUIPPED;
  }

  public enum Result {
    EQUIPPED,
    EMPTY_HAND
  }
}
