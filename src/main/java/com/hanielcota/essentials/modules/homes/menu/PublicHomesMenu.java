package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickHandler;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.HomesPublicMenuConfig;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.homes.menu.presentation.HomeEntryRenderer;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class PublicHomesMenu implements EssentialsMenu {

  public static final String ID = "essentials.homes.public";

  private static final int MIN_ROWS = 1;

  private final ConfigHandle<HomesConfig> config;
  private final HomeService homeService;
  private final HomeEntryRenderer renderer;
  private final PublicHomesClickHandler clickHandler;

  private static HomesPublicMenuConfig publicConfig(@NonNull HomesConfig snap) {
    var menu = snap.menu();
    return menu.publicMenu();
  }

  private static int menuRows(@NonNull HomesPublicMenuConfig snap) {
    var configuredRows = snap.rows();
    return MenuLayouts.clampRows(configuredRows);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = publicConfig(this.config.value());

    var pagination = buildPaginationConfig(snap, menus);
    var rawTitle = snap.title();
    var menuTitle = ComponentUtils.mini(rawTitle);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(menuRows(snap));
    builder.title(menuTitle);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    builder.buildAndRegister();
  }

  private static List<Integer> resolveContentSlots(@NonNull HomesPublicMenuConfig snap, int rows) {
    var rowSlotCount = MenuLayouts.slotCount(rows);
    var fallbackSlots = MenuLayouts.fallbackContentSlots(rows, rowSlotCount);

    var configuredSlots = snap.contentSlots();
    return MenuLayouts.sanitizeSlots(configuredSlots, rows, fallbackSlots);
  }

  private PaginationConfig buildPaginationConfig(
      @NonNull HomesPublicMenuConfig snap, @NonNull MenuService menus) {
    var rows = menuRows(snap);
    var slots = resolveContentSlots(snap, rows);

    var paginationBuilder = PaginationConfig.builder().contentSlots(slots);

    if (rows > MIN_ROWS) {
      var navigation = snap.navigation();
      PageNavigation.apply(menus, paginationBuilder, ID, rows, navigation);
    }

    return paginationBuilder.build();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var snap = publicConfig(this.config.value());
    var rows = menuRows(snap);

    var homes = this.homeService.publicHomes();

    if (homes.isEmpty()) {
      var emptySlot = buildEmptySlot(snap, rows);
      return List.of(emptySlot);
    }

    var slots = new ArrayList<SlotDefinition>(homes.size());
    addEntrySlots(slots, homes);

    return slots;
  }

  private void addEntrySlots(@NonNull List<SlotDefinition> slots, @NonNull List<Home> homes) {
    for (var home : homes) {
      var template = this.renderer.render(home);

      ClickHandler onClick = click -> this.clickHandler.visit(click, home);
      var slot = SlotDefinition.of(-1, template, onClick);
      slots.add(slot);
    }
  }

  private SlotDefinition buildEmptySlot(@NonNull HomesPublicMenuConfig snap, int rows) {
    var contentSlots = resolveContentSlots(snap, rows);
    var centerIndex = contentSlots.size() / 2;
    var centerSlot = contentSlots.get(centerIndex);

    var material = snap.emptyMaterial();
    var name = snap.emptyName();
    var lore = snap.emptyLore();
    var template = MenuTemplates.info(material, name, lore);

    ClickHandler noop = click -> {};

    return SlotDefinition.of(centerSlot, template, noop);
  }
}
