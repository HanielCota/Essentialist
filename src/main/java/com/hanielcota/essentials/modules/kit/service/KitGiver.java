package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.modules.kit.domain.Kit;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** Adds a kit's items to a player's inventory, dropping the overflow when configured. */
public final class KitGiver {

  /** Gives {@code kit} to {@code player}; returns {@code true} when overflow was dropped. */
  public boolean give(@NonNull Player player, @NonNull Kit kit, boolean dropOverflow) {
    var inventory = player.getInventory();
    var clones = kit.items().stream().map(ItemStack::clone).toArray(ItemStack[]::new);

    var leftover = inventory.addItem(clones);
    if (leftover.isEmpty() || !dropOverflow) {
      return false;
    }

    var world = player.getWorld();
    var location = player.getLocation();
    for (var item : leftover.values()) {
      world.dropItemNaturally(location, item);
    }

    return true;
  }
}
