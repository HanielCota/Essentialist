package com.hanielcota.essentials.modules.compact.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.compact.config.CompactConfig;
import com.hanielcota.essentials.util.ItemStacks;
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
      var isWanted = wanted.contains(item.getType());

      if (plain && isWanted) {
        totals.merge(item.getType(), item.getAmount(), Integer::sum);
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
        var take = Math.min(remaining, item.getAmount());
        var left = item.getAmount() - take;
        remaining -= take;

        if (left == 0) {
          inv.setItem(slot, null);
        }
        if (left > 0) {
          item.setAmount(left);
        }
      }
    }
  }

  public int compact(@NonNull Player player) {
    var recipes = this.config.value().recipes();
    var inv = player.getInventory();
    var totals = countByMaterial(inv, recipes.keySet());

    var blocksCompacted = 0;
    for (var entry : totals.entrySet()) {
      var ingredient = entry.getKey();
      var total = entry.getValue();
      var recipe = recipes.get(ingredient);
      var unit = recipe.amount();
      var blocks = unit > 0 ? total / unit : 0;

      if (blocks > 0) {
        removeFromInventory(inv, ingredient, blocks * unit);
        var produced = new ItemStack(recipe.block(), blocks);
        var overflow = inv.addItem(produced);

        for (var drop : overflow.values()) {
          player.getWorld().dropItem(player.getLocation(), drop);
        }
        blocksCompacted += blocks;
      }
    }
    return blocksCompacted;
  }
}
