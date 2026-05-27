package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.DynamicContentProvider;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.builder.MenuBuilder;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.List;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Wires the "MiniMessage title + pagination + dynamic content" shape used by the TPA settings,
 * pickers and action menus. Sits next to {@link PaginatedInfoMenus} (which adds a static info slot
 * + nav buttons) and exists so individual menu classes don't all repeat the same builder block —
 * the repetition was 47% duplication on Sonar's new-code metric before this was extracted.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaginatedMenus {

  public static void register(
      @NonNull MenuService menus,
      @NonNull String menuId,
      int rows,
      @NonNull String titleMini,
      @NonNull List<Integer> contentSlots,
      @NonNull DynamicContentProvider contentProvider) {
    register(menus, menuId, rows, titleMini, contentSlots, contentProvider, builder -> {});
  }

  public static void register(
      @NonNull MenuService menus,
      @NonNull String menuId,
      int rows,
      @NonNull String titleMini,
      @NonNull List<Integer> contentSlots,
      @NonNull DynamicContentProvider contentProvider,
      @NonNull Consumer<MenuBuilder> builderConfigurer) {
    var title = ComponentUtils.mini(titleMini);
    var pagination = PaginationConfig.builder().contentSlots(contentSlots).build();

    var builder = MenuFramework.builder(menuId, menus);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(contentProvider);

    builderConfigurer.accept(builder);

    builder.buildAndRegister();
  }
}
