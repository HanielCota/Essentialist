package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickHandler;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.back.service.BackEntryProvider;
import com.hanielcota.essentials.modules.back.service.BackFilterState;
import com.hanielcota.essentials.modules.back.service.BackStaffViewState;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.Cause;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public final class BackMenu implements EssentialsMenu {

  public static final String ID = "essentials.back";

  private static final int MIN_ROWS = 1;
  private static final int STAFF_INFO_SLOT = 40;

  private final ConfigHandle<BackConfig> config;
  private final BackEntryRenderer renderer;
  private final BackClickHandler clickHandler;
  private final BackEntryProvider entryProvider;
  private final BackFilterState filterState;
  private final BackStaffViewState staffViewState;

  private static int menuRows(@NonNull BackConfig snap) {
    var configuredRows = snap.menuRows();
    return MenuLayouts.clampRows(configuredRows);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();

    var pagination = buildPaginationConfig(snap, menus);
    var rawTitle = snap.menuTitle();
    var menuTitle = ComponentUtils.mini(rawTitle);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(menuRows(snap));
    builder.title(menuTitle);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    builder.buildAndRegister();
  }

  private static List<Integer> resolveContentSlots(@NonNull BackConfig snap, int rows) {
    var rowSlotCount = MenuLayouts.slotCount(rows);
    var fallbackSize = Math.min(rowSlotCount, TeleportHistory.CAPACITY);

    var configuredSlots = snap.menuContentSlots();
    var fallbackSlots = MenuLayouts.fallbackContentSlots(rows, fallbackSize);
    return MenuLayouts.sanitizeSlots(configuredSlots, rows, fallbackSlots);
  }

  private PaginationConfig buildPaginationConfig(
      @NonNull BackConfig snap, @NonNull MenuService menus) {
    var rows = menuRows(snap);
    var slots = resolveContentSlots(snap, rows);

    var paginationBuilder = PaginationConfig.builder().contentSlots(slots);

    if (rows > MIN_ROWS) {
      var navigation = snap.navigation();
      PageNavigation.apply(menus, paginationBuilder, ID, rows, navigation);
    }

    return paginationBuilder.build();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var snap = this.config.value();
    var rows = menuRows(snap);

    var playerId = player.getUniqueId();
    var allEntries = this.entryProvider.entriesFor(playerId);

    var filter = this.filterState.filterOf(playerId);
    var entries = BackFilter.apply(allEntries, filter);

    var slots = new ArrayList<SlotDefinition>(entries.size() + 1);

    buildStaffInfoSlot(slots, snap, rows, playerId);

    if (entries.isEmpty()) {
      var emptySlot = buildEmptySlot(snap, rows);
      slots.add(emptySlot);
    } else {
      addEntrySlots(slots, entries);
    }

    var filterSlot = buildFilterSlot(snap, rows, filter);
    slots.add(filterSlot);

    return slots;
  }

  private void addEntrySlots(
      @NonNull List<SlotDefinition> slots, @NonNull List<HistoryEntry> entries) {
    for (var i = 0; i < entries.size(); i++) {
      var entry = entries.get(i);
      var humanIndex = i + 1;
      var template = this.renderer.render(entry, humanIndex);

      ClickHandler onClick = click -> this.clickHandler.handle(click, entry);
      var slot = SlotDefinition.of(-1, template, onClick);
      slots.add(slot);
    }
  }

  private SlotDefinition buildEmptySlot(@NonNull BackConfig snap, int rows) {
    var contentSlots = resolveContentSlots(snap, rows);
    var centerIndex = contentSlots.size() / 2;
    var centerSlot = contentSlots.get(centerIndex);

    var template = this.renderer.renderEmpty();
    ClickHandler noop = click -> {};

    return SlotDefinition.of(centerSlot, template, noop);
  }

  private void buildStaffInfoSlot(
      @NonNull List<SlotDefinition> slots,
      @NonNull BackConfig snap,
      int rows,
      @NonNull java.util.UUID playerId) {
    var targetName = this.staffViewState.targetNameOf(playerId);
    if (targetName == null) {
      return;
    }

    var titleTemplate = snap.staffViewTitle();
    var title = titleTemplate.replace("{player}", targetName);

    var safeSlot = MenuLayouts.sanitizeSlot(STAFF_INFO_SLOT, rows, 0);

    var template = ItemTemplate.builder(Material.NAME_TAG).name(title).italic(false).build();

    ClickHandler noop = click -> {};
    var slot = SlotDefinition.of(safeSlot, template, noop);
    slots.add(slot);
  }

  private SlotDefinition buildFilterSlot(
      @NonNull BackConfig snap, int rows, @Nullable Cause filter) {
    var filterLabel = BackFilter.filterLabel(snap, filter);

    var nameTemplate = snap.filterName();
    var name = nameTemplate.replace("{filter}", filterLabel);

    var lore = BackFilter.renderFilterLore(snap, filterLabel, filter);
    var loreArray = lore.toArray(String[]::new);

    var icon = snap.filterIcon();
    var template = ItemTemplate.builder(icon).name(name).lore(loreArray).italic(false).build();

    var configuredSlot = snap.filterSlot();
    var safeSlot = MenuLayouts.sanitizeSlot(configuredSlot, rows, 0);

    return SlotDefinition.of(safeSlot, template, this.clickHandler::onFilterClicked);
  }
}
