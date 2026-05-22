package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public final class BackMenu implements Menu {

  public static final String ID = "essentials.back";

  private final ConfigHandle<BackConfig> config;
  private final TeleportHistory history;
  private final BackEntryRenderer renderer;
  private final BackClickHandler clickHandler;

  public BackMenu(
      ConfigHandle<BackConfig> config,
      TeleportHistory history,
      BackEntryRenderer renderer,
      BackClickHandler clickHandler) {
    this.config = Objects.requireNonNull(config, "config");
    this.history = Objects.requireNonNull(history, "history");
    this.renderer = Objects.requireNonNull(renderer, "renderer");
    this.clickHandler = Objects.requireNonNull(clickHandler, "clickHandler");
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    Objects.requireNonNull(menus, "menus");
    var snap = config.value();
    var pagination = PaginationConfig.builder().contentSlots(snap.menuContentSlots()).build();

    MenuFramework.builder(ID, menus)
        .rows(snap.menuRows())
        .title(ComponentUtils.mini(snap.menuTitle()))
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(Player player, MenuSession session) {
    var entries = history.list(player.getUniqueId());
    var slots = new ArrayList<SlotDefinition>(entries.size());

    for (int i = 0; i < entries.size(); i++) {
      var entry = entries.get(i);
      var template = renderer.render(entry, i + 1);
      slots.add(SlotDefinition.of(-1, template, click -> clickHandler.handle(click, entry)));
    }

    return slots;
  }
}
