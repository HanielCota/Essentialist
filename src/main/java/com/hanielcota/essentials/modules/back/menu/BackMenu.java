package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class BackMenu implements EssentialsMenu {

  public static final String ID = "essentials.back";

  private final ConfigHandle<BackConfig> config;
  private final TeleportHistory history;
  private final BackEntryRenderer renderer;
  private final BackClickHandler clickHandler;
  private final BackMenuState state;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var rows = MenuLayouts.clampRows(snap.menuRows());
    var fallbackSize = Math.min(MenuLayouts.slotCount(rows), TeleportHistory.CAPACITY);
    var slots =
        MenuLayouts.sanitizeSlots(
            snap.menuContentSlots(), rows, MenuLayouts.fallbackContentSlots(rows, fallbackSize));
    var pagination = PaginationConfig.builder().contentSlots(slots).build();

    var menuTitle = ComponentUtils.mini(snap.menuTitle());
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

    for (var i = 0; i < entries.size(); i++) {
      var entry = entries.get(i);
      var template = this.renderer.render(entry, i + 1);
      slots.add(SlotDefinition.of(-1, template, click -> this.clickHandler.handle(click, entry)));
    }

    return slots;
  }
}
