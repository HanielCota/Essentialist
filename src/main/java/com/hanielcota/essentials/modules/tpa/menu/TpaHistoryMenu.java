package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaMenuConfig;
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Read-only menu of a player's last {@link TpaHistory#CAPACITY} sent teleport requests. Items are
 * display-only — clicking them does nothing.
 */
@RequiredArgsConstructor
public final class TpaHistoryMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpahistory";

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

    var pagination = PaginationConfig.builder().contentSlots(slots).build();

    var rawTitle = settings.title();
    var menuTitle = ComponentUtils.mini(rawTitle);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(menuTitle);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var playerId = player.getUniqueId();

    var prefetched = this.state.consumePrefetch(playerId);
    var entries = prefetched != null ? prefetched : this.history.list(playerId);

    if (entries.isEmpty()) {
      return emptyState();
    }

    var slots = new ArrayList<SlotDefinition>(entries.size());
    for (var i = 0; i < entries.size(); i++) {
      var entry = entries.get(i);
      var humanIndex = i + 1;

      var template = this.renderer.render(entry, humanIndex);
      var slot = SlotDefinition.of(-1, template, click -> {});

      slots.add(slot);
    }
    return slots;
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
