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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public record CompactService(ConfigHandle<CompactConfig> config) {

  private static Map<Material, Integer> countByMaterial(
      @NonNull Inventory inv, @NonNull Set<Material> wanted) {
    Map<Material, Integer> totals = new EnumMap<>(Material.class);
    for (var slot = 0; slot < inv.getSize(); slot++) {
      var item = inv.getItem(slot);
      if (item != null && ItemStacks.isPlain(item) && wanted.contains(item.getType())) {
        totals.merge(item.getType(), item.getAmount(), Integer::sum);
      }
    }
    return totals;
  }

  private static void removeFromInventory(
      @NonNull Inventory inv, @NonNull Material material, int amount) {
    var remaining = amount;
    for (var slot = 0; slot < inv.getSize() && remaining > 0; slot++) {
      var item = inv.getItem(slot);
      if (item != null && item.getType() == material && ItemStacks.isPlain(item)) {
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
