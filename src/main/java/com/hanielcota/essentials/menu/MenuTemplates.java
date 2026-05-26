package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MenuTemplates {

  public static @NonNull ItemTemplate simple(@NonNull Material material, @NonNull String name) {
    return simple(material, name, List.of());
  }

  public static @NonNull ItemTemplate simple(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);

    return builder.build();
  }

  public static @NonNull ItemTemplate info(
      @NonNull Material material, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.flags(ItemFlag.HIDE_ATTRIBUTES);
    builder.italic(false);

    return builder.build();
  }
}
