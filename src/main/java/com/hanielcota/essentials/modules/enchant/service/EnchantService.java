package com.hanielcota.essentials.modules.enchant.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.enchant.config.EnchantConfig;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class EnchantService {

  private final ConfigHandle<EnchantConfig> config;

  /** Adds an enchantment to the held item, gated by the configured limits and block list. */
  public ApplyResult apply(@NonNull Player player, @NonNull Enchantment enchantment, int level) {
    var held = player.getInventory().getItemInMainHand();
    if (held.getType().isAir()) {
      return ApplyResult.EMPTY_HAND;
    }

    var snap = this.config.value();
    var key = enchantment.getKey().getKey();

    if (snap.isBlocked(key)) {
      return ApplyResult.BLOCKED;
    }
    if (level > snap.maxLevel()) {
      return ApplyResult.LEVEL_TOO_HIGH;
    }

    if (snap.allowUnsafe()) {
      held.addUnsafeEnchantment(enchantment, level);
      return ApplyResult.APPLIED;
    }

    if (!enchantment.canEnchantItem(held)) {
      return ApplyResult.INCOMPATIBLE;
    }
    if (level > enchantment.getMaxLevel()) {
      return ApplyResult.LEVEL_TOO_HIGH;
    }

    held.addEnchantment(enchantment, level);
    return ApplyResult.APPLIED;
  }

  /** Removes one enchantment from the held item. */
  public RemoveResult remove(@NonNull Player player, @NonNull Enchantment enchantment) {
    var held = player.getInventory().getItemInMainHand();
    if (held.getType().isAir()) {
      return RemoveResult.EMPTY_HAND;
    }

    if (!held.containsEnchantment(enchantment)) {
      return RemoveResult.NOT_ENCHANTED;
    }

    held.removeEnchantment(enchantment);

    return RemoveResult.REMOVED;
  }

  /** Removes every enchantment from the held item. */
  public ClearResult clearAll(@NonNull Player player) {
    var held = player.getInventory().getItemInMainHand();
    if (held.getType().isAir()) {
      return ClearResult.EMPTY_HAND;
    }

    var enchantments = held.getEnchantments();
    if (enchantments.isEmpty()) {
      return ClearResult.NOTHING_TO_CLEAR;
    }

    // Snapshot the keys before iterating — Bukkit returns an unmodifiable copy today, but the
    // contract leaves room for implementations that would throw on concurrent removal.
    var originalKeys = enchantments.keySet();
    var keys = Set.copyOf(originalKeys);
    keys.forEach(held::removeEnchantment);

    return new ClearResult.Cleared(enchantments.size());
  }

  public enum ApplyResult {
    APPLIED,
    EMPTY_HAND,
    LEVEL_TOO_HIGH,
    BLOCKED,
    INCOMPATIBLE
  }

  public enum RemoveResult {
    REMOVED,
    NOT_ENCHANTED,
    EMPTY_HAND
  }

  /**
   * Outcome of {@link #clearAll(Player)} — sealed so callers can switch exhaustively. {@code
   * Cleared} carries the removed count; the other two are flag-only states.
   */
  public sealed interface ClearResult {
    ClearResult NOTHING_TO_CLEAR = NothingToClear.INSTANCE;
    ClearResult EMPTY_HAND = EmptyHand.INSTANCE;

    record Cleared(int removed) implements ClearResult {}

    enum NothingToClear implements ClearResult {
      INSTANCE
    }

    enum EmptyHand implements ClearResult {
      INSTANCE
    }
  }
}
