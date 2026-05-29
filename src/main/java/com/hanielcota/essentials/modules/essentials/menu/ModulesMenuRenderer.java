package com.hanielcota.essentials.modules.essentials.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.module.control.ModuleControl;
import com.hanielcota.essentials.modules.essentials.config.EssentialsConfig;
import com.hanielcota.essentials.modules.essentials.config.ModulesMenuConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds the module-control menu slots: the selected category's modules as toggle items in the
 * content area, plus one category tab per {@link ModuleCategory} on the bottom row.
 */
@RequiredArgsConstructor
public final class ModulesMenuRenderer {

  private static final int ROW_WIDTH = 9;

  private final @NonNull ConfigHandle<EssentialsConfig> config;
  private final @NonNull ModuleControl control;

  public List<SlotDefinition> slots(
      @NonNull ModuleCategory selected,
      @NonNull BiConsumer<ClickContext, ModuleCategory> categorySwitcher,
      @NonNull BiConsumer<ClickContext, String> toggler) {
    var snap = this.config.value();
    var menu = snap.menu();
    var rows = menu.effectiveRows();
    var tabRowStart = (rows - 1) * ROW_WIDTH;

    var slots = new ArrayList<SlotDefinition>();

    appendModules(slots, menu, selected, tabRowStart, toggler);
    appendCategoryTabs(slots, selected, tabRowStart, categorySwitcher);

    return slots;
  }

  private void appendModules(
      @NonNull List<SlotDefinition> slots,
      @NonNull ModulesMenuConfig menu,
      @NonNull ModuleCategory selected,
      int contentLimit,
      @NonNull BiConsumer<ClickContext, String> toggler) {
    var moduleIds = modulesOf(selected);

    var slot = 0;
    for (var moduleId : moduleIds) {
      if (slot >= contentLimit) {
        break;
      }

      var def = moduleItem(menu, slot, moduleId, toggler);
      slots.add(def);
      slot++;
    }
  }

  private void appendCategoryTabs(
      @NonNull List<SlotDefinition> slots,
      @NonNull ModuleCategory selected,
      int tabRowStart,
      @NonNull BiConsumer<ClickContext, ModuleCategory> categorySwitcher) {
    var slot = tabRowStart;
    for (var category : ModuleCategory.values()) {
      var def = categoryTab(slot, category, selected, categorySwitcher);
      slots.add(def);
      slot++;
    }
  }

  private List<String> modulesOf(@NonNull ModuleCategory category) {
    return this.control.moduleIds().stream()
        .filter(id -> ModuleCategoryCatalog.categoryOf(id) == category)
        .toList();
  }

  private SlotDefinition moduleItem(
      @NonNull ModulesMenuConfig menu,
      int slot,
      @NonNull String moduleId,
      @NonNull BiConsumer<ClickContext, String> toggler) {
    var enabled = this.control.persistedEnabled(moduleId);
    var pending = this.control.pendingRestart(moduleId);

    var material = menu.material(enabled);
    var name = menu.name(enabled, moduleId);
    var lore = menu.lore(enabled, pending);
    var loreArray = lore.toArray(String[]::new);

    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.lore(loreArray);
    builder.italic(false);
    builder.glow(pending);

    var template = builder.build();

    return SlotDefinition.of(slot, template, click -> toggler.accept(click, moduleId));
  }

  private SlotDefinition categoryTab(
      int slot,
      @NonNull ModuleCategory category,
      @NonNull ModuleCategory selected,
      @NonNull BiConsumer<ClickContext, ModuleCategory> categorySwitcher) {
    var icon = category.icon();
    var label = category.label();

    var builder = ItemTemplate.builder(icon);
    builder.name(label);
    builder.italic(false);
    builder.glow(category == selected);

    var template = builder.build();

    return SlotDefinition.of(slot, template, click -> categorySwitcher.accept(click, category));
  }
}
