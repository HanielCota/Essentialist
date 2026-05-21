package com.hanielcota.essentials.modules.hat.service;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public final class HatService {

  public Result equip(Player player) {
    PlayerInventory inv = player.getInventory();
    ItemStack held = inv.getItemInMainHand();

    if (held.getType().isAir()) {
      return Result.EMPTY_HAND;
    }

    ItemStack previousHelmet = inv.getHelmet();
    inv.setHelmet(held);
    inv.setItemInMainHand(previousHelmet);
    return Result.EQUIPPED;
  }

  public enum Result {
    EQUIPPED,
    EMPTY_HAND
  }
}
