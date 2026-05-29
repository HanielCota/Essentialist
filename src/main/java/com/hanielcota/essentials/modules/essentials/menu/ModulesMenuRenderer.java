package com.hanielcota.essentials.modules.essentials.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ClickHandler;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.module.control.ModuleControl;
import com.hanielcota.essentials.modules.essentials.config.EssentialsConfig;
import com.hanielcota.essentials.modules.essentials.config.ModulesMenuConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds the module-control menu slots: a static guide item, the selected category's modules as
 * toggle items placed in the configured content slots, and a single cycling category filter button
 * (same control style as the warps filter).
 */
@RequiredArgsConstructor
public final class ModulesMenuRenderer {

  private static final int ROW_WIDTH = 9;

  private final @NonNull ConfigHandle<EssentialsConfig> config;
  private final @NonNull ModuleControl control;
  private final @NonNull ModulesFilterRenderer filterRenderer;

  public List<SlotDefinition> slots(
      @NonNull ModuleCategory selected,
      @NonNull ClickHandler onCycle,
      @NonNull BiConsumer<ClickContext, String> toggler) {
    var snap = this.config.value();
    var menu = snap.menu();
    var rows = menu.effectiveRows();

    var slots = new ArrayList<SlotDefinition>();

    appendModules(slots, menu, selected, toggler);
    appendInfo(slots, menu, rows);
    appendFilter(slots, menu, selected, rows, onCycle);

    return slots;
  }

  private void appendModules(
      @NonNull List<SlotDefinition> slots,
      @NonNull ModulesMenuConfig menu,
      @NonNull ModuleCategory selected,
      @NonNull BiConsumer<ClickContext, String> toggler) {
    var contentSlots = menu.effectiveContentSlots();
    var moduleIds = modulesOf(selected);
    var count = Math.min(moduleIds.size(), contentSlots.size());

    for (var i = 0; i < count; i++) {
      var slot = contentSlots.get(i);
      var moduleId = moduleIds.get(i);
      var def = moduleItem(menu, slot, moduleId, toggler);

      slots.add(def);
    }
  }

  private void appendInfo(
      @NonNull List<SlotDefinition> slots, @NonNull ModulesMenuConfig menu, int rows) {
    var info = menu.info();
    var infoSlot = MenuLayouts.sanitizeSlot(info.slot(), rows, 4);

    var template = MenuTemplates.simple(info.material(), info.name(), info.lore());

    slots.add(SlotDefinition.of(infoSlot, template, click -> {}));
  }

  private void appendFilter(
      @NonNull List<SlotDefinition> slots,
      @NonNull ModulesMenuConfig menu,
      @NonNull ModuleCategory selected,
      int rows,
      @NonNull ClickHandler onCycle) {
    var filter = menu.filter();
    var fallbackSlot = (rows - 1) * ROW_WIDTH;
    var filterSlot = MenuLayouts.sanitizeSlot(filter.slot(), rows, fallbackSlot);

    var filterItem = this.filterRenderer.render(filter, selected);

    slots.add(SlotDefinition.of(filterSlot, filterItem, onCycle));
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
}
