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
    var previousId = menuId + ".previous";
    var nextId = menuId + ".next";
    menus.registerTemplate(previousId, button(config.material(), config.previousName()));
    menus.registerTemplate(nextId, button(config.material(), config.nextName()));
    pagination
        .navigationSlots(
            List.of(config.effectivePreviousSlot(rows), config.effectiveNextSlot(rows)))
        .previousTemplate(previousId)
        .nextTemplate(nextId);
  }

  private static ItemTemplate button(@NonNull Material material, @NonNull String name) {
    return ItemTemplate.builder(material).name(name).italic(false).build();
  }
}
