package com.hanielcota.essentials.modules.compact.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.compact.config.CompactConfig;
import com.hanielcota.essentials.shared.ItemStacks;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public record CompactService(ConfigHandle<CompactConfig> config) {

  /**
   * Counts wanted materials across the 36 storage slots only.
   *
   * <p>Iterating {@code inv.getSize()} would also walk armor and the off-hand. Off-hand items would
   * be removed by {@link #removeFromInventory} but the produced blocks only land in storage via
   * {@code addItem}, so a player with iron in the off-hand and a full main inventory would see the
   * produced block dropped at their feet while the off-hand slot was silently emptied.
   */
  private static Map<Material, Integer> countByMaterial(
      @NonNull PlayerInventory inv, @NonNull Set<Material> wanted) {
    Map<Material, Integer> totals = new EnumMap<>(Material.class);
    var storage = inv.getStorageContents();

    for (var item : storage) {
      if (item == null) continue;

      var plain = ItemStacks.isPlain(item);
      var type = item.getType();
      var isWanted = wanted.contains(type);

      if (plain && isWanted) {
        var amount = item.getAmount();
        totals.merge(type, amount, Integer::sum);
      }
    }
    return totals;
  }

  private static void removeFromInventory(
      @NonNull PlayerInventory inv, @NonNull Material material, int amount) {
    var remaining = amount;
    var storage = inv.getStorageContents();

    for (var slot = 0; slot < storage.length && remaining > 0; slot++) {
      var item = storage[slot];
      if (item == null) continue;

      var typeMatches = item.getType() == material;
      var plain = ItemStacks.isPlain(item);

      if (typeMatches && plain) {
        var currentAmount = item.getAmount();
        var take = Math.min(remaining, currentAmount);
        var left = currentAmount - take;
        remaining -= take;

        if (left == 0) {
          inv.setItem(slot, null);
        }
        if (left > 0) {
          item.setAmount(left);
          inv.setItem(slot, item);
        }
      }
    }
  }

  private static void dropOverflow(
      @NonNull Player player, @NonNull Map<Integer, ItemStack> overflow) {
    if (overflow.isEmpty()) {
      return;
    }
    var world = player.getWorld();
    var location = player.getLocation();

    for (var drop : overflow.values()) {
      world.dropItem(location, drop);
    }
  }

  public int compact(@NonNull Player player) {
    var snap = this.config.value();
    var recipes = snap.recipes();
    var inv = player.getInventory();
    var wanted = recipes.keySet();
    var totals = countByMaterial(inv, wanted);

    var blocksCompacted = 0;
    for (var entry : totals.entrySet()) {
      var ingredient = entry.getKey();
      var total = entry.getValue();
      var recipe = recipes.get(ingredient);
      var unit = recipe.amount();
      var blocks = unit > 0 ? total / unit : 0;

      if (blocks <= 0) continue;

      var toRemove = blocks * unit;
      removeFromInventory(inv, ingredient, toRemove);

      var producedType = recipe.block();
      var produced = new ItemStack(producedType, blocks);
      var overflow = inv.addItem(produced);

      dropOverflow(player, overflow);
      blocksCompacted += blocks;
    }
    return blocksCompacted;
  }
}
