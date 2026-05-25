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
    var snap = this.config.value();
    var menuSpec = snap.menu();

    var rows = menuSpec.effectiveCategoryRows();
    var titleText = menuSpec.categoryTitle();
    var title = ComponentUtils.mini(titleText);
    var contentSlots = menuSpec.effectiveCategoryContentSlots();

    var paginationBuilder = PaginationConfig.builder().contentSlots(contentSlots);
    var pagination = paginationBuilder.build();

    var builder = MenuFramework.builder(ID, menusRef);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    var menu = builder.build();
    menu.register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var categories = MaterialCategory.browsable();
    var slots = new ArrayList<SlotDefinition>(categories.size() + 1);

    for (var category : categories) {
      if (category == MaterialCategory.MISC) {
        continue;
      }

      var template = representativeItem(category);
      var slot = SlotDefinition.of(-1, template, ctx -> this.clickHandler.handle(ctx, category));

      slots.add(slot);
    }

    var backSlot = backButtonSlot();
    slots.add(backSlot);

    return slots;
  }

  private @NonNull SlotDefinition backButtonSlot() {
    var snap = this.config.value();
    var menuSpec = snap.menu();

    var material = menuSpec.categoryBackMaterial();
    var name = menuSpec.categoryBackName();
    var lore = menuSpec.categoryBackLore();
    var loreArray = lore.toArray(String[]::new);

    var templateBuilder = ItemTemplate.builder(material);
    templateBuilder.name(name);
    templateBuilder.lore(loreArray);
    templateBuilder.italic(false);
    var template = templateBuilder.build();

    var slot = menuSpec.effectiveCategoryBackSlot();

    return SlotDefinition.of(slot, template, this.clickHandler::back);
  }

  private @NonNull ItemTemplate representativeItem(@NonNull MaterialCategory category) {
    var snap = this.config.value();
    var menuSpec = snap.menu();

    var icon = this.icons.iconFor(category);
    var categoryName = menuSpec.categoryName(category);
    var name = menuSpec.formatCategoryItemName(categoryName);
    var lore = menuSpec.formatCategoryItemLore(categoryName);

    var builder = ItemTemplate.builder(icon);
    builder.name(name);
    builder.lore(lore);
    builder.italic(false);

    return builder.build();
  }
}
