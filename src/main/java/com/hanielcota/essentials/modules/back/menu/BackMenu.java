package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickHandler;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.back.service.BackEntryProvider;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class BackMenu implements EssentialsMenu {

  public static final String ID = "essentials.back";

  private static final int MIN_ROWS = 1;

  private final ConfigHandle<BackConfig> config;
  private final BackEntryRenderer renderer;
  private final BackClickHandler clickHandler;
  private final BackEntryProvider entryProvider;

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

  private static int menuRows(@NonNull BackConfig snap) {
    var configuredRows = snap.menuRows();
    return MenuLayouts.clampRows(configuredRows);
  }

  private PaginationConfig buildPaginationConfig(
      @NonNull BackConfig snap, @NonNull MenuService menus) {
    var rows = menuRows(snap);
    var rowSlotCount = MenuLayouts.slotCount(rows);
    var fallbackSize = Math.min(rowSlotCount, TeleportHistory.CAPACITY);

    var configuredSlots = snap.menuContentSlots();
    var fallbackSlots = MenuLayouts.fallbackContentSlots(rows, fallbackSize);
    var slots = MenuLayouts.sanitizeSlots(configuredSlots, rows, fallbackSlots);

    var paginationBuilder = PaginationConfig.builder().contentSlots(slots);

    if (rows > MIN_ROWS) {
      var navigation = snap.navigation();
      PageNavigation.apply(menus, paginationBuilder, ID, rows, navigation);
    }

    return paginationBuilder.build();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var playerId = player.getUniqueId();
    var entries = this.entryProvider.entriesFor(playerId);

    var slots = new ArrayList<SlotDefinition>(entries.size());
    for (var i = 0; i < entries.size(); i++) {
      var entry = entries.get(i);
      var humanIndex = i + 1;
      var template = this.renderer.render(entry, humanIndex);

      ClickHandler onClick = click -> this.clickHandler.handle(click, entry);
      var slot = SlotDefinition.of(-1, template, onClick);
      slots.add(slot);
    }

    return slots;
  }
}
