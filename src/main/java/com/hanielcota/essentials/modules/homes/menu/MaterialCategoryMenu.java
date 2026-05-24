package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategoryIconRegistry;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Category selector for the material picker. Shows one representative item per category (Combat,
 * Decoration, Minerals, etc). Clicking a category stores the choice in {@link HomesActionTarget}
 * and opens the paginated {@link MaterialPickerMenu}.
 */
@RequiredArgsConstructor
public final class MaterialCategoryMenu implements EssentialsMenu {

  public static final String ID = "essentials.homes.categories";

  private final ConfigHandle<HomesConfig> config;
  private final MaterialCategoryClickHandler clickHandler;
  private final MaterialCategoryIconRegistry icons;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menusRef) {
    var menuSpec = this.config.value().menu();
    var rows = menuSpec.effectiveCategoryRows();
    var title = ComponentUtils.mini(menuSpec.categoryTitle());

    var pagination =
        PaginationConfig.builder().contentSlots(menuSpec.effectiveCategoryContentSlots()).build();

    MenuFramework.builder(ID, menusRef)
        .rows(rows)
        .title(title)
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var categories = MaterialCategory.browsable();
    var slots = new ArrayList<SlotDefinition>(categories.size() + 1);

    for (var category : categories) {
      if (category == MaterialCategory.MISC) {
        continue;
      }
      var template = representativeItem(category);
      slots.add(SlotDefinition.of(-1, template, ctx -> this.clickHandler.handle(ctx, category)));
    }

    slots.add(backButtonSlot());

    return slots;
  }

  private @NonNull SlotDefinition backButtonSlot() {
    var menuSpec = this.config.value().menu();
    var back =
        ItemTemplate.builder(menuSpec.categoryBackMaterial())
            .name(menuSpec.categoryBackName())
            .lore(menuSpec.categoryBackLore().toArray(String[]::new))
            .italic(false)
            .build();

    return SlotDefinition.of(menuSpec.effectiveCategoryBackSlot(), back, this.clickHandler::back);
  }

  private @NonNull ItemTemplate representativeItem(@NonNull MaterialCategory category) {
    var menuSpec = this.config.value().menu();
    var icon = this.icons.iconFor(category);
    var categoryName = category.displayName();
    var name = menuSpec.formatCategoryItemName(categoryName);
    var lore = menuSpec.formatCategoryItemLore(categoryName);

    return ItemTemplate.builder(icon).name(name).lore(lore).italic(false).build();
  }
}
