package com.hanielcota.essentials.modules.tpa.menu.history;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaHistoryEntryRenderer;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Paginated history menu showing the player's resolved teleport requests. Includes a status filter
 * cycle button and click-to-copy destination feedback for ACCEPTED entries.
 */
@RequiredArgsConstructor
public final class TpaHistoryMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpahistory";

  private static final int MIN_ROWS = 1;

  private final ConfigHandle<TpaConfig> config;
  private final TpaHistory history;
  private final TpaHistoryEntryRenderer renderer;
  private final TpaHistoryMenuState state;
  private final TpaHistoryClickHandler clicks;

  private static List<Integer> resolveContentSlots(@NonNull TpaMenuConfig settings, int rows) {
    var totalSlots = MenuLayouts.slotCount(rows);
    var capacity = TpaHistory.CAPACITY;
    var fallbackSize = Math.min(totalSlots, capacity);

    var configured = settings.contentSlots();
    var fallback = MenuLayouts.fallbackContentSlots(rows, fallbackSize);

    return MenuLayouts.sanitizeSlots(configured, rows, fallback);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var settings = snap.menu();

    var rows = MenuLayouts.clampRows(settings.rows());
    var slots = resolveContentSlots(settings, rows);

    var paginationBuilder = PaginationConfig.builder().contentSlots(slots);
    if (rows > MIN_ROWS) {
      PageNavigation.apply(menus, paginationBuilder, ID, rows, settings.navigation());
    }
    var pagination = paginationBuilder.build();

    var rawTitle = settings.title();
    var menuTitle = ComponentUtils.mini(rawTitle);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(menuTitle);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);
    builder.slot(
        backSlot(settings, rows), backTemplate(settings), click -> click.switchTo(TpaHelpMenu.ID));

    builder.buildAndRegister();
  }

  private static int backSlot(@NonNull TpaMenuConfig settings, int rows) {
    return MenuLayouts.sanitizeSlot(settings.backSlot(), rows, 0);
  }

  private static ItemTemplate backTemplate(@NonNull TpaMenuConfig settings) {
    var builder = ItemTemplate.builder(settings.backIcon());
    builder.name(settings.backName());
    builder.lore(settings.backLore().toArray(String[]::new));
    builder.italic(false);
    return builder.build();
  }

  private static ItemTemplate filterTemplate(
      @NonNull TpaMenuConfig settings, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(settings.filterIcon());
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);
    return builder.build();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var snap = this.config.value();
    var settings = snap.menu();
    var rows = MenuLayouts.clampRows(settings.rows());

    var playerId = player.getUniqueId();
    var prefetched = this.state.consumePrefetch(playerId);
    var allEntries = prefetched != null ? prefetched : this.history.list(playerId);
    var filter = this.state.filterOf(playerId);
    var entries = TpaHistoryFilter.apply(allEntries, filter);

    var slots = new ArrayList<SlotDefinition>();

    if (entries.isEmpty()) {
      slots.addAll(emptyState());
    } else {
      var humanIndex = 1;
      for (var entry : entries) {
        slots.add(entrySlot(entry, humanIndex));
        humanIndex++;
      }
    }

    slots.add(filterSlot(settings, rows, filter));

    return slots;
  }

  private SlotDefinition entrySlot(@NonNull TpaHistoryEntry entry, int humanIndex) {
    var template = this.renderer.render(entry, humanIndex);
    return SlotDefinition.of(-1, template, click -> this.clicks.onEntryClicked(click, entry));
  }

  private SlotDefinition filterSlot(
      @NonNull TpaMenuConfig settings, int rows, @Nullable TeleportRequestStatus filter) {
    var filterLabel = filter == null ? settings.filterAll() : settings.statusLabel(filter);
    var name = settings.filterName().replace("{filter}", filterLabel);
    var lore = TpaHistoryFilter.renderFilterLore(settings, filterLabel, filter);
    var template = filterTemplate(settings, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.filterSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this.clicks::onFilterClicked);
  }

  private List<SlotDefinition> emptyState() {
    var snap = this.config.value();
    var settings = snap.menu();

    var rows = MenuLayouts.clampRows(settings.rows());
    var contentSlots = resolveContentSlots(settings, rows);
    var centerSlot = contentSlots.get(contentSlots.size() / 2);

    var template = this.renderer.renderEmpty();
    var slot = SlotDefinition.of(centerSlot, template, click -> {});

    return List.of(slot);
  }
}
