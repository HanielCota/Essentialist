package com.hanielcota.essentials.modules.kit.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

/**
 * Renders a kit item as a read-only preview template. Carries the item's material, amount, custom
 * name and lore, plus a glow when it is enchanted. Exact NBT/enchant detail is not reproduced (a
 * MenuFramework limitation), but the visible identity of the item is preserved.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KitItemPreviewRenderer {

  public static ItemTemplate render(@NonNull ItemStack item) {
    var builder = ItemTemplate.builder(item.getType());
    builder.amount(item.getAmount());
    builder.italic(false);

    if (!item.getEnchantments().isEmpty()) {
      builder.glow(true);
    }

    var meta = item.getItemMeta();
    if (meta != null && meta.hasDisplayName()) {
      builder.name(meta.displayName());
    }
    if (meta != null && meta.hasLore()) {
      builder.lore(meta.lore());
    }

    return builder.build();
  }
}
