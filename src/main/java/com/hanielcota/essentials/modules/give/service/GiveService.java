package com.hanielcota.essentials.modules.give.service;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class GiveService {

  /** Gives {@code amount} of {@code material} to the player, returning how many did not fit. */
  public int give(Player player, Material material, int amount) {
    var leftovers = player.getInventory().addItem(new ItemStack(material, amount));

    int leftover = 0;
    for (var stack : leftovers.values()) {
      leftover += stack.getAmount();
    }
    return leftover;
  }
}
