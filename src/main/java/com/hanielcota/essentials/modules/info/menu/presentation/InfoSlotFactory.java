package com.hanielcota.essentials.modules.info.menu.presentation;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.menu.InfoTab;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class InfoSlotFactory {

  static SlotDefinition entryItem(int slot, @NonNull InfoEntry entry) {
    var icon = entry.icon();
    var name = entry.name();
    var lore = entry.lore();
    var loreArray = lore.toArray(String[]::new);
    var headOwner = entry.headOwner();

    var builder = ItemTemplate.builder(icon);
    builder = builder.name(name);
    builder = builder.lore(loreArray);
    builder = builder.italic(false);

    if (headOwner != null) {
      builder = builder.head(headOwner);
    }

    var template = builder.build();

    return SlotDefinition.of(slot, template, click -> {});
  }

  static SlotDefinition back(
      @NonNull InfoConfig snap, @NonNull BiConsumer<ClickContext, InfoTab> tabSwitcher) {
    var material = snap.backMaterial();
    var name = snap.backName();
    var lore = snap.backLore();
    var template = MenuTemplates.simple(material, name, lore);
    var backSlot = snap.effectiveBackSlot();

    return SlotDefinition.of(
        backSlot, template, click -> tabSwitcher.accept(click, InfoTab.CATEGORIES));
  }

  static SlotDefinition category(
      int slot,
      @NonNull Material icon,
      @NonNull String name,
      @NonNull List<String> lore,
      @NonNull InfoTab target,
      @NonNull BiConsumer<ClickContext, InfoTab> tabSwitcher) {
    var template = MenuTemplates.simple(icon, name, lore);

    return SlotDefinition.of(slot, template, click -> tabSwitcher.accept(click, target));
  }
}
