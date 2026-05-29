package com.hanielcota.essentials.modules.itemlore.service;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.Nullable;

/** Edits the lore of the item in the player's main hand. */
public final class ItemLoreService {

  public Result add(@NonNull Player player, @NonNull Component line) {
    var item = heldItem(player);
    if (item == null) {
      return Result.EMPTY_HAND;
    }

    var lore = currentLore(item);
    lore.add(line);

    applyLore(item, lore);
    return Result.ADDED;
  }

  public Result set(@NonNull Player player, int humanLine, @NonNull Component line) {
    var item = heldItem(player);
    if (item == null) {
      return Result.EMPTY_HAND;
    }

    var lore = currentLore(item);
    var index = humanLine - 1;
    if (index < 0 || index >= lore.size()) {
      return Result.INVALID_LINE;
    }

    lore.set(index, line);

    applyLore(item, lore);
    return Result.UPDATED;
  }

  public Result remove(@NonNull Player player, int humanLine) {
    var item = heldItem(player);
    if (item == null) {
      return Result.EMPTY_HAND;
    }

    var lore = currentLore(item);
    var index = humanLine - 1;
    if (index < 0 || index >= lore.size()) {
      return Result.INVALID_LINE;
    }

    lore.remove(index);

    applyLore(item, lore.isEmpty() ? null : lore);
    return Result.REMOVED;
  }

  public Result clear(@NonNull Player player) {
    var item = heldItem(player);
    if (item == null) {
      return Result.EMPTY_HAND;
    }

    var lore = currentLore(item);
    if (lore.isEmpty()) {
      return Result.EMPTY_LORE;
    }

    applyLore(item, null);
    return Result.CLEARED;
  }

  private static @Nullable ItemStack heldItem(@NonNull Player player) {
    var inventory = player.getInventory();
    var item = inventory.getItemInMainHand();

    if (item.getType().isAir()) {
      return null;
    }

    return item;
  }

  private static List<Component> currentLore(@NonNull ItemStack item) {
    var meta = item.getItemMeta();
    var lore = meta.lore();

    if (lore == null) {
      return new ArrayList<>();
    }

    return new ArrayList<>(lore);
  }

  private static void applyLore(@NonNull ItemStack item, @Nullable List<Component> lore) {
    var meta = item.getItemMeta();
    meta.lore(lore);

    item.setItemMeta(meta);
  }

  public enum Result {
    ADDED,
    UPDATED,
    REMOVED,
    CLEARED,
    EMPTY_HAND,
    INVALID_LINE,
    EMPTY_LORE
  }
}
