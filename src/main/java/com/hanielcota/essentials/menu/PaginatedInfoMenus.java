package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.DynamicContentProvider;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.builder.MenuBuilder;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Wires the common "paginated list + static info item + page nav" menu shape used by {@code
 * WhitelistMenu}, {@code VanishMenu} and any future list-style menus. Centralising it avoids the
 * line-for-line duplication between near-identical menu classes and makes future tweaks to
 * navigation/info-slot wiring land in a single place.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginatedInfoMenus {

  private static final int MIN_ROWS_FOR_PAGE_NAV = 1;

  public static void register(
      @NonNull MenuService menus,
      @NonNull String menuId,
      int rows,
      @NonNull String titleMini,
      @NonNull List<Integer> contentSlots,
      @NonNull NavigationButtonsConfig navigation,
      int infoSlot,
      @NonNull ItemTemplate infoTemplate,
      @NonNull DynamicContentProvider contentProvider) {
    register(
        menus,
        menuId,
        rows,
        titleMini,
        contentSlots,
        navigation,
        infoSlot,
        infoTemplate,
        contentProvider,
        builder -> {});
  }

  public static void register(
      @NonNull MenuService menus,
      @NonNull String menuId,
      int rows,
      @NonNull String titleMini,
      @NonNull List<Integer> contentSlots,
      @NonNull NavigationButtonsConfig navigation,
      int infoSlot,
      @NonNull ItemTemplate infoTemplate,
      @NonNull DynamicContentProvider contentProvider,
      @NonNull Consumer<MenuBuilder> builderConfigurer) {
    var paginationBuilder = PaginationConfig.builder();
    paginationBuilder.contentSlots(contentSlots);

    if (rows > MIN_ROWS_FOR_PAGE_NAV) {
      PageNavigation.apply(menus, paginationBuilder, menuId, rows, navigation);
    }

    var titleComponent = ComponentUtils.mini(titleMini);
    var paginationConfig = paginationBuilder.build();

    var menuBuilder = MenuFramework.builder(menuId, menus);
    menuBuilder.rows(rows);
    menuBuilder.title(titleComponent);
    menuBuilder.pagination(paginationConfig);
    menuBuilder.slot(infoSlot, infoTemplate, null);
    menuBuilder.dynamicContent(contentProvider);

    builderConfigurer.accept(menuBuilder);

    menuBuilder.buildAndRegister();
  }
}
