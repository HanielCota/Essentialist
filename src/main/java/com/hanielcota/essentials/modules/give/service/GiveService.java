package com.hanielcota.essentials.modules.give.service;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class GiveService {

  /** Gives {@code amount} of {@code material} to the player, returning how many did not fit. */
  public int give(@NonNull Player player, @NonNull Material material, int amount) {
    return giveResult(player, material, amount).leftover();
  }

  public GiveResult giveResult(@NonNull Player player, @NonNull Material material, int amount) {
    var stack = new ItemStack(material, amount);
    var leftovers = player.getInventory().addItem(stack);

    var leftover = 0;
    for (var stackEntry : leftovers.values()) {
      leftover += stackEntry.getAmount();
    }
    return GiveResult.of(amount, leftover);
  }
}
