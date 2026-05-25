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

  private static @NonNull ItemTemplate buildInfoTemplate(@NonNull HomesMenuConfig menuSpec) {
    var infoMaterial = menuSpec.infoMaterial();
    var infoName = menuSpec.infoName();
    var infoLore = menuSpec.infoLore();
    var loreArray = infoLore.toArray(String[]::new);

    var builder = ItemTemplate.builder(infoMaterial);
    builder.name(infoName);
    builder.lore(loreArray);
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
    var menuSpec = snap.menu();

    var rows = menuSpec.effectiveRows();
    var titleText = menuSpec.title();
    var menuTitle = ComponentUtils.mini(titleText);
    var contentSlots = menuSpec.effectiveContentSlots();

    var paginationBuilder = PaginationConfig.builder().contentSlots(contentSlots);
    if (rows > MIN_ROWS) {
      var navigation = menuSpec.navigation();
      PageNavigation.apply(menus, paginationBuilder, ID, rows, navigation);
    }
    var pagination = paginationBuilder.build();

    var infoTemplate = buildInfoTemplate(menuSpec);
    var infoSlot = menuSpec.effectiveInfoSlot();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(menuTitle);
    builder.pagination(pagination);
    builder.allowShiftClick(true);
    builder.slot(infoSlot, infoTemplate, null);
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var uuid = player.getUniqueId();
    var prefetched = this.state.consumePrefetch(uuid);
    var entries = prefetched != null ? prefetched : this.service.list(uuid);

    var slots = new ArrayList<SlotDefinition>(entries.size());

    for (var i = 0; i < entries.size(); i++) {
      var home = entries.get(i);
      var template = this.renderer.render(home);
      var slot = SlotDefinition.of(-1, template, click -> this.clickHandler.handle(click, home));

      slots.add(slot);
    }

    return slots;
  }
}
