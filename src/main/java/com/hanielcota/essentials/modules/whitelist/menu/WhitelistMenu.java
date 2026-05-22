package com.hanielcota.essentials.modules.whitelist.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public final class WhitelistMenu implements Menu {

  public static final String ID = "essentials.whitelist";

  private static final int MIN_ROWS = 1;
  private static final int MAX_ROWS = 6;
  private static final int SLOTS_PER_ROW = 9;

  private final ConfigHandle<WhitelistConfig> config;
  private final WhitelistService service;
  private final WhitelistEntryRenderer renderer;
  private final WhitelistClickHandler clickHandler;

  public WhitelistMenu(
      ConfigHandle<WhitelistConfig> config,
      WhitelistService service,
      WhitelistEntryRenderer renderer,
      WhitelistClickHandler clickHandler) {
    this.config = Objects.requireNonNull(config, "config");
    this.service = Objects.requireNonNull(service, "service");
    this.renderer = Objects.requireNonNull(renderer, "renderer");
    this.clickHandler = Objects.requireNonNull(clickHandler, "clickHandler");
  }

  /** Content slots cover every row but the last, which is left for pagination controls. */
  private static List<Integer> contentSlots(int rows) {
    int count = rows > MIN_ROWS ? (rows - 1) * SLOTS_PER_ROW : SLOTS_PER_ROW;
    return IntStream.range(0, count).boxed().toList();
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    Objects.requireNonNull(menus, "menus");
    var snap = config.value();
    int rows = Math.clamp(snap.menuRows(), MIN_ROWS, MAX_ROWS);
    var pagination = PaginationConfig.builder().contentSlots(contentSlots(rows)).build();

    MenuFramework.builder(ID, menus)
        .rows(rows)
        .title(ComponentUtils.mini(snap.menuTitle()))
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(Player player, MenuSession session) {
    var whitelisted = service.list();
    var slots = new ArrayList<SlotDefinition>(whitelisted.size());
    for (var entry : whitelisted) {
      var template = renderer.render(entry);
      slots.add(SlotDefinition.of(-1, template, click -> clickHandler.handle(click, entry)));
    }
    return slots;
  }
}
