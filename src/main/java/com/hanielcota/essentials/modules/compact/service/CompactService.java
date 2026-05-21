package com.hanielcota.essentials.modules.compact.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.compact.config.CompactConfig;
import com.hanielcota.essentials.modules.compact.config.CompactConfig.Recipe;
import com.hanielcota.essentials.util.ItemStacks;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public record CompactService(ConfigHandle<CompactConfig> config) {

  public CompactService {
    Objects.requireNonNull(config, "config");
  }

  private static Map<Material, Integer> countByMaterial(Inventory inv, Set<Material> wanted) {
    Map<Material, Integer> totals = new EnumMap<>(Material.class);
    for (int slot = 0; slot < inv.getSize(); slot++) {
      var item = inv.getItem(slot);
      if (item != null && ItemStacks.isPlain(item) && wanted.contains(item.getType())) {
        totals.merge(item.getType(), item.getAmount(), Integer::sum);
      }
    }
    return totals;
  }

  private static void removeFromInventory(Inventory inv, Material material, int amount) {
    int remaining = amount;
    for (int slot = 0; slot < inv.getSize() && remaining > 0; slot++) {
      var item = inv.getItem(slot);
      if (item == null || item.getType() != material || !ItemStacks.isPlain(item)) {
        continue;
      }
      int take = Math.min(remaining, item.getAmount());
      int left = item.getAmount() - take;
      if (left == 0) {
        inv.setItem(slot, null);
      } else {
        item.setAmount(left);
      }
      remaining -= take;
    }
  }

  public int compact(Player player) {
    var recipes = config.value().recipes();
    var inv = player.getInventory();
    var totals = countByMaterial(inv, recipes.keySet());
    int blocksCompacted = 0;

    for (var entry : totals.entrySet()) {
      var ingredient = entry.getKey();
      int total = entry.getValue();
      Recipe recipe = recipes.get(ingredient);
      int blocks = total / recipe.amount();
      if (blocks == 0) {
        continue;
      }
      removeFromInventory(inv, ingredient, blocks * recipe.amount());
      var produced = new ItemStack(recipe.block(), blocks);
      var overflow = inv.addItem(produced);
      for (var drop : overflow.values()) {
        player.getWorld().dropItem(player.getLocation(), drop);
      }
      blocksCompacted += blocks;
    }
    return blocksCompacted;
  }
}
