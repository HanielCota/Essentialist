package com.hanielcota.essentials.modules.enchant.config;

import java.util.List;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record EnchantConfig(
    @Comment("Highest level /enchant will apply.") int maxLevel,
    @Comment(
            "Allow levels above the vanilla cap and enchanting incompatible items. When false, only"
                + " vanilla-valid enchantment/item/level combinations are accepted.")
        boolean allowUnsafe,
    @Comment("Enchantment keys /enchant refuses to apply, e.g. [\"mending\", \"sharpness\"].")
        List<String> blockedEnchantments,
    @Comment("Shown when the player is not holding an item.") String emptyHand,
    @Comment("Shown after applying. Placeholders: {enchantment}, {level}.") String applied,
    @Comment("Shown after removing. Placeholder: {enchantment}.") String removed,
    @Comment("Shown when the held item lacks the enchantment. Placeholder: {enchantment}.")
        String notEnchanted,
    @Comment("Shown after /enchant clear. Placeholder: {count}.") String cleared,
    @Comment("Shown when /enchant clear finds no enchantments.") String nothingToClear,
    @Comment("Shown when the level is below 1.") String invalidLevel,
    @Comment("Shown when the level is above the max. Placeholder: {max}.") String levelTooHigh,
    @Comment("Shown when the enchantment is blocked. Placeholder: {enchantment}.") String blocked,
    @Comment(
            "Shown when the item can't take the enchantment and unsafe mode is off. Placeholder:"
                + " {enchantment}.")
        String incompatible) {

  public static EnchantConfig defaults() {
    return new EnchantConfig(
        10,
        true,
        List.of(),
        "<red>You need to be holding an item.",
        "<green>Enchantment <gold>{enchantment} {level}</gold> applied.",
        "<green>Enchantment <gold>{enchantment}</gold> removed.",
        "<red>The item does not have the <gold>{enchantment}</gold> enchantment.",
        "<green>Enchantments removed: <gold>{count}</gold>.",
        "<red>The item has no enchantments.",
        "<red>The level must be at least 1.",
        "<red>The maximum level is <gold>{max}</gold>.",
        "<red>The <gold>{enchantment}</gold> enchantment is blocked.",
        "<red>The item cannot receive the <gold>{enchantment}</gold> enchantment.");
  }

  public boolean isBlocked(@NonNull String enchantmentKey) {
    for (var entry : blockedEnchantments) {
      if (entry.equalsIgnoreCase(enchantmentKey)) {
        return true;
      }
    }
    return false;
  }

  public String formatApplied(@NonNull String enchantment, int level) {
    var levelText = Integer.toString(level);
    var withEnchant = applied.replace("{enchantment}", enchantment);
    return withEnchant.replace("{level}", levelText);
  }

  public String formatRemoved(@NonNull String enchantment) {
    return removed.replace("{enchantment}", enchantment);
  }

  public String formatNotEnchanted(@NonNull String enchantment) {
    return notEnchanted.replace("{enchantment}", enchantment);
  }

  public String formatCleared(int count) {
    var countText = Integer.toString(count);
    return cleared.replace("{count}", countText);
  }

  public String formatLevelTooHigh(int effectiveMax) {
    var maxText = Integer.toString(effectiveMax);
    return levelTooHigh.replace("{max}", maxText);
  }

  public String formatBlocked(@NonNull String enchantment) {
    return blocked.replace("{enchantment}", enchantment);
  }

  public String formatIncompatible(@NonNull String enchantment) {
    return incompatible.replace("{enchantment}", enchantment);
  }
}
