package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;

/**
 * Registers previous/next page templates and wires them into a {@link PaginationConfig.Builder}.
 * The {@code NavigationRenderer} in the framework removes the enchantment glint automatically when
 * a button is at the edge of the page range, so the buttons get visual enable/disable for free.
 */
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

    var previousTemplate = button(material, previousName);
    var nextTemplate = button(material, nextName);

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

  private static ItemTemplate button(@NonNull Material material, @NonNull String name) {
    var builder = ItemTemplate.builder(material);
    builder.name(name);
    builder.italic(false);

    return builder.build();
  }
}
