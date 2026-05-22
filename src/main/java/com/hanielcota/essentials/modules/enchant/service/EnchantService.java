package com.hanielcota.essentials.modules.enchant.service;

import java.util.Objects;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

public final class EnchantService {

  /** Adds an enchantment to the held item at any level — unsafe, no vanilla checks. */
  public Result apply(Player player, Enchantment enchantment, int level) {
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(enchantment, "enchantment");

    var held = player.getInventory().getItemInMainHand();
    if (held.getType().isAir()) {
      return Result.EMPTY_HAND;
    }

    held.addUnsafeEnchantment(enchantment, level);
    return Result.APPLIED;
  }

  /** Removes one enchantment from the held item. */
  public Result remove(Player player, Enchantment enchantment) {
    Objects.requireNonNull(player, "player");
    Objects.requireNonNull(enchantment, "enchantment");

    var held = player.getInventory().getItemInMainHand();
    if (held.getType().isAir()) {
      return Result.EMPTY_HAND;
    }

    if (!held.containsEnchantment(enchantment)) {
      return Result.NOT_ENCHANTED;
    }

    held.removeEnchantment(enchantment);
    return Result.REMOVED;
  }

  /**
   * Removes every enchantment from the held item.
   *
   * @return the number of enchantments removed, or {@code -1} when the hand is empty
   */
  public int clearAll(Player player) {
    Objects.requireNonNull(player, "player");

    var held = player.getInventory().getItemInMainHand();
    if (held.getType().isAir()) {
      return -1;
    }

    var enchantments = held.getEnchantments();
    if (enchantments.isEmpty()) {
      return 0;
    }

    enchantments.keySet().forEach(held::removeEnchantment);
    return enchantments.size();
  }

  public enum Result {
    APPLIED,
    REMOVED,
    NOT_ENCHANTED,
    EMPTY_HAND
  }
}
