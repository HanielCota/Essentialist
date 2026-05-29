package com.hanielcota.essentials.modules.more.service;

import lombok.NonNull;
import org.bukkit.entity.Player;

/** Fills the held item stack to its maximum size. */
public final class MoreService {

  public Result fill(@NonNull Player player) {
    var inventory = player.getInventory();
    var item = inventory.getItemInMainHand();

    if (item.getType().isAir()) {
      return Result.EMPTY_HAND;
    }

    var max = item.getMaxStackSize();
    if (item.getAmount() >= max) {
      return Result.ALREADY_FULL;
    }

    item.setAmount(max);
    return Result.FILLED;
  }

  public enum Result {
    FILLED,
    EMPTY_HAND,
    ALREADY_FULL
  }
}
