package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.HomesMenuConfig;
import com.hanielcota.essentials.modules.homes.menu.presentation.HomeEntryRenderer;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class HomesMenu implements EssentialsMenu {

  public static final String ID = "essentials.homes";

  private static final int MIN_ROWS = 1;

  private final ConfigHandle<HomesConfig> config;
  private final HomeService service;
  private final HomeEntryRenderer renderer;
  private final HomeClickHandler clickHandler;
  private final HomesMenuState state;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var menuSpec = this.config.value().menu();
    var rows = menuSpec.effectiveRows();
    var menuTitle = ComponentUtils.mini(menuSpec.title());
    var contentSlots = menuSpec.effectiveContentSlots();

    var paginationBuilder = PaginationConfig.builder().contentSlots(contentSlots);
    if (rows > MIN_ROWS) {
      PageNavigation.apply(menus, paginationBuilder, ID, rows, menuSpec.navigation());
    }
    var pagination = paginationBuilder.build();

    var infoTemplate = buildInfoTemplate(menuSpec);

    MenuFramework.builder(ID, menus)
        .rows(rows)
        .title(menuTitle)
        .pagination(pagination)
        .slot(menuSpec.effectiveInfoSlot(), infoTemplate, null)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private static @NonNull ItemTemplate buildInfoTemplate(@NonNull HomesMenuConfig menuSpec) {
    return ItemTemplate.builder(menuSpec.infoMaterial())
        .name(menuSpec.infoName())
        .lore(menuSpec.infoLore().toArray(String[]::new))
        .italic(false)
        .build();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var uuid = player.getUniqueId();
    var entries = this.state.consumePrefetch(uuid);

    if (entries == null) {
      entries = this.service.list(uuid);
    }

    var slots = new ArrayList<SlotDefinition>(entries.size());

    for (var i = 0; i < entries.size(); i++) {
      var home = entries.get(i);
      var template = this.renderer.render(home);

      slots.add(SlotDefinition.of(-1, template, click -> this.clickHandler.handle(click, home)));
    }

    return slots;
  }
}
