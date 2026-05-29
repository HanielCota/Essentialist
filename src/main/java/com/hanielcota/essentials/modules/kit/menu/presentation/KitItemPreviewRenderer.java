package com.hanielcota.essentials.modules.kit.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

/**
 * Renders a kit item as a read-only preview template. Carries the item's material, amount, name and
 * lore, plus a glow when it is enchanted. Items without a custom name fall back to the vanilla
 * translatable name (otherwise the slot would show a blank name). Exact NBT/enchant detail is not
 * reproduced — a MenuFramework limitation — but the visible identity of the item is preserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KitItemPreviewRenderer {

  public static ItemTemplate render(@NonNull ItemStack item) {
    var builder = ItemTemplate.builder(item.getType());
    builder.amount(item.getAmount());
    builder.name(nameOf(item));

    if (!item.getEnchantments().isEmpty()) {
      builder.glow(true);
    }

    var meta = item.getItemMeta();
    if (meta != null && meta.hasLore()) {
      builder.lore(meta.lore());
    }

    return builder.build();
  }

  private static Component nameOf(@NonNull ItemStack item) {
    var meta = item.getItemMeta();
    if (meta != null && meta.hasDisplayName()) {
      return meta.displayName();
    }

    return Component.translatable(item.translationKey());
  }
}
