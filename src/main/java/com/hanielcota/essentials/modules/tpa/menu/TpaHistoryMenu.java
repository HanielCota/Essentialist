package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
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
import com.hanielcota.essentials.modules.tpa.domain.Destination;
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

  private static List<Integer> resolveContentSlots(@NonNull TpaMenuConfig settings, int rows) {
    var totalSlots = MenuLayouts.slotCount(rows);
    var capacity = TpaHistory.CAPACITY;
    var fallbackSize = Math.min(totalSlots, capacity);

    var configured = settings.contentSlots();
    var fallback = MenuLayouts.fallbackContentSlots(rows, fallbackSize);

    return MenuLayouts.sanitizeSlots(configured, rows, fallback);
  }

  private static String formatDestinationCopy(
      @NonNull String template, @NonNull Destination destination) {
    return template
        .replace("{world}", destination.world())
        .replace("{x}", Long.toString(Math.round(destination.x())))
        .replace("{y}", Long.toString(Math.round(destination.y())))
        .replace("{z}", Long.toString(Math.round(destination.z())));
  }

  private static List<String> renderFilterLore(
      @NonNull TpaMenuConfig settings,
      @NonNull String filterLabel,
      @Nullable TeleportRequestStatus filter) {
    var lines = new ArrayList<String>(settings.filterLore().size() + 4);
    for (var template : settings.filterLore()) {
      if (template.contains("{options}")) {
        lines.addAll(filterOptions(settings, filter));
        continue;
      }
      lines.add(template.replace("{filter}", filterLabel));
    }
    return lines;
  }

  private static List<String> filterOptions(
      @NonNull TpaMenuConfig settings, @Nullable TeleportRequestStatus current) {
    var marker = settings.filterActiveMarker();
    return List.of(
        markActive(settings.filterAll(), marker, current == null),
        markActive(settings.statusAccepted(), marker, current == TeleportRequestStatus.ACCEPTED),
        markActive(settings.statusDenied(), marker, current == TeleportRequestStatus.DENIED),
        markActive(settings.statusExpired(), marker, current == TeleportRequestStatus.EXPIRED),
        markActive(settings.statusCancelled(), marker, current == TeleportRequestStatus.CANCELLED));
  }

  private static String markActive(@NonNull String label, @NonNull String marker, boolean active) {
    return active ? label + marker : label;
  }

  private static List<TpaHistoryEntry> applyFilter(
      @NonNull List<TpaHistoryEntry> entries, @Nullable TeleportRequestStatus filter) {
    if (filter == null) {
      return entries;
    }
    var filtered = new ArrayList<TpaHistoryEntry>(entries.size());
    for (var entry : entries) {
      if (entry.status() == filter) {
        filtered.add(entry);
      }
    }
    return filtered;
  }

  private static ItemTemplate filterTemplate(
      @NonNull TpaMenuConfig settings, @NonNull String name, @NonNull List<String> lore) {
    var builder = ItemTemplate.builder(settings.filterIcon());
    builder.name(name);
    builder.lore(lore.toArray(String[]::new));
    builder.italic(false);
    return builder.build();
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

    var menu = builder.build();
    menu.register();
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

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var snap = this.config.value();
    var settings = snap.menu();
    var rows = MenuLayouts.clampRows(settings.rows());

    var playerId = player.getUniqueId();
    var prefetched = this.state.consumePrefetch(playerId);
    var allEntries = prefetched != null ? prefetched : this.history.list(playerId);
    var filter = this.state.filterOf(playerId);
    var entries = applyFilter(allEntries, filter);

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
    return SlotDefinition.of(-1, template, click -> onEntryClicked(click, entry));
  }

  private void onEntryClicked(@NonNull ClickContext click, @NonNull TpaHistoryEntry entry) {
    if (entry.status() != TeleportRequestStatus.ACCEPTED) {
      return;
    }
    var destination = entry.destination();
    if (destination == null) {
      return;
    }

    var settings = this.config.value().menu();
    var copyMsg = formatDestinationCopy(settings.destinationCopyMessage(), destination);
    click.reply(copyMsg);
  }

  private SlotDefinition filterSlot(
      @NonNull TpaMenuConfig settings, int rows, @Nullable TeleportRequestStatus filter) {
    var filterLabel = filter == null ? settings.filterAll() : settings.statusLabel(filter);
    var name = settings.filterName().replace("{filter}", filterLabel);
    var lore = renderFilterLore(settings, filterLabel, filter);
    var template = filterTemplate(settings, name, lore);
    var safeSlot = MenuLayouts.sanitizeSlot(settings.filterSlot(), rows, 0);

    return SlotDefinition.of(safeSlot, template, this::onFilterClicked);
  }

  private void onFilterClicked(@NonNull ClickContext click) {
    var viewerId = click.player().getUniqueId();
    this.state.cycleFilter(viewerId);
    click.refresh();
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
