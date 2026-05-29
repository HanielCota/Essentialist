package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.modules.kit.domain.Kit;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/** Adds a kit's items to a player's inventory, dropping or refusing the overflow per config. */
public final class KitGiver {

  /**
   * Gives {@code kit} to {@code player}. When {@code dropOverflow} is off and the items would not
   * all fit, nothing is given and {@link GiveResult#REJECTED_FULL} is returned so the caller can
   * skip the cooldown — items are never silently lost.
   */
  public GiveResult give(@NonNull Player player, @NonNull Kit kit, boolean dropOverflow) {
    var items = kit.items();

    if (!dropOverflow && !fits(player, items)) {
      return GiveResult.REJECTED_FULL;
    }

    var inventory = player.getInventory();
    var leftover = inventory.addItem(cloneAll(items));
    if (leftover.isEmpty()) {
      return GiveResult.GIVEN;
    }

    dropAll(player, leftover.values());
    return GiveResult.OVERFLOW_DROPPED;
  }

  // Non-destructive fit check: replay the add into a throwaway inventory seeded with a copy of the
  // player's storage, so stacking rules are honoured without touching the live inventory.
  private static boolean fits(@NonNull Player player, @NonNull List<ItemStack> items) {
    var storage = player.getInventory().getStorageContents();

    var probe = Bukkit.createInventory(null, storage.length);
    probe.setContents(cloneContents(storage));

    var leftover = probe.addItem(cloneAll(items));
    return leftover.isEmpty();
  }

  private static void dropAll(@NonNull Player player, @NonNull Collection<ItemStack> items) {
    var world = player.getWorld();
    var location = player.getLocation();

    for (var item : items) {
      world.dropItemNaturally(location, item);
    }
  }

  private static ItemStack[] cloneAll(@NonNull List<ItemStack> items) {
    return items.stream().map(ItemStack::clone).toArray(ItemStack[]::new);
  }

  private static ItemStack[] cloneContents(@NonNull ItemStack[] contents) {
    var copy = new ItemStack[contents.length];
    for (var i = 0; i < contents.length; i++) {
      copy[i] = contents[i] == null ? null : contents[i].clone();
    }

    return copy;
  }

  public enum GiveResult {
    GIVEN,
    OVERFLOW_DROPPED,
    REJECTED_FULL
  }
}
