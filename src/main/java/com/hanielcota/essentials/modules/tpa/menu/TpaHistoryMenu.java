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
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Read-only menu of a player's last {@link TpaHistory#CAPACITY} sent teleport requests. Items are
 * display-only â€” clicking them does nothing.
 */
@RequiredArgsConstructor
public final class TpaHistoryMenu implements EssentialsMenu {

  public static final String ID = "essentials.tpahistory";

  private final ConfigHandle<TpaConfig> config;
  private final TpaHistory history;
  private final TpaHistoryEntryRenderer renderer;
  private final TpaHistoryMenuState state;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var settings = this.config.value().menu();
    int rows = MenuLayouts.clampRows(settings.rows());
    var fallbackSize = Math.min(MenuLayouts.slotCount(rows), TpaHistory.CAPACITY);
    var slots =
        MenuLayouts.sanitizeSlots(
            settings.contentSlots(), rows, MenuLayouts.fallbackContentSlots(rows, fallbackSize));
    var pagination = PaginationConfig.builder().contentSlots(slots).build();

    var menuTitle = ComponentUtils.mini(settings.title());
    MenuFramework.builder(ID, menus)
        .rows(rows)
        .title(menuTitle)
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var entries = this.state.consumePrefetch(player.getUniqueId());
    if (entries == null) {
      entries = this.history.list(player.getUniqueId());
    }
    var slots = new ArrayList<SlotDefinition>(entries.size());
    for (int i = 0; i < entries.size(); i++) {
      var template = this.renderer.render(entries.get(i), i + 1);
      slots.add(SlotDefinition.of(-1, template, click -> {}));
    }
    return slots;
  }
}
