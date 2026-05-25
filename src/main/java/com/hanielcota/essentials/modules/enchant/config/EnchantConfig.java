package com.hanielcota.essentials.modules.enchant.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record EnchantConfig(
    @Comment("Shown when the player is not holding an item.") String emptyHand,
    @Comment("Shown after applying. Placeholders: {enchantment}, {level}.") String applied,
    @Comment("Shown after removing. Placeholder: {enchantment}.") String removed,
    @Comment("Shown when the held item lacks the enchantment. Placeholder: {enchantment}.")
        String notEnchanted,
    @Comment("Shown after /enchant clear. Placeholder: {count}.") String cleared,
    @Comment("Shown when /enchant clear finds no enchantments.") String nothingToClear,
    @Comment("Shown when the level is below 1.") String invalidLevel) {

  public static EnchantConfig defaults() {
    return new EnchantConfig(
        "<red>Você precisa segurar um item.",
        "<green>Encantamento <gold>{enchantment} {level}</gold> aplicado.",
        "<green>Encantamento <gold>{enchantment}</gold> removido.",
        "<red>O item não possui o encantamento <gold>{enchantment}</gold>.",
        "<green>Encantamentos removidos: <gold>{count}</gold>.",
        "<red>O item não possui encantamentos.",
        "<red>O nível precisa ser no mínimo 1.");
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
}
