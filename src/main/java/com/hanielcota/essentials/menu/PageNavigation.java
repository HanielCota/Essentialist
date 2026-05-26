package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageNavigation {

  public static void apply(
      @NonNull MenuService menus,
      @NonNull PaginationConfig.Builder pagination,
      @NonNull String menuId,
      int rows,
      @NonNull NavigationButtonsConfig config) {
    var material = config.material();
    var previousName = config.previousName();
    var nextName = config.nextName();

    var previousId = menuId + ".previous";
    var nextId = menuId + ".next";

    var previousTemplate = MenuTemplates.simple(material, previousName);
    var nextTemplate = MenuTemplates.simple(material, nextName);

    menus.registerTemplate(previousId, previousTemplate);
    menus.registerTemplate(nextId, nextTemplate);

    var previousSlot = config.effectivePreviousSlot(rows);
    var nextSlot = config.effectiveNextSlot(rows);
    var navigationSlots = List.of(previousSlot, nextSlot);

    pagination.navigationSlots(navigationSlots);
    pagination.previousTemplate(previousId);
    pagination.nextTemplate(nextId);
    pagination.hideDisabledNavigation(true);
  }
}
