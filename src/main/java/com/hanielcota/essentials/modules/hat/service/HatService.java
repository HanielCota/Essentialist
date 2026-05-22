package com.hanielcota.essentials.modules.hat.service;

import org.bukkit.entity.Player;

public final class HatService {

  public Result equip(Player player) {
    var inv = player.getInventory();
    var held = inv.getItemInMainHand();

    if (held.getType().isAir()) {
      return Result.EMPTY_HAND;
    }

    var previousHelmet = inv.getHelmet();
    inv.setHelmet(held);
    inv.setItemInMainHand(previousHelmet);
    return Result.EQUIPPED;
  }

  public enum Result {
    EQUIPPED,
    EMPTY_HAND
  }
}
