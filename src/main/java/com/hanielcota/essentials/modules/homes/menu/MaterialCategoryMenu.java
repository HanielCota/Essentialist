package com.hanielcota.essentials.modules.homes.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickHandler;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.builder.MenuBuilder;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.MaterialCategorySection;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

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

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menusRef) {
    var snap = this.config.value();
    var menuSpec = snap.menu();

    var rows = MaterialCategorySection.rows(menuSpec);
    var titleText = menuSpec.categoryTitle();
    var title = ComponentUtils.mini(titleText);
    var contentSlots = MaterialCategorySection.contentSlots(menuSpec);

    var paginationBuilder = PaginationConfig.builder().contentSlots(contentSlots);
    var pagination = paginationBuilder.build();

    var builder = MenuFramework.builder(ID, menusRef);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);
    registerBackButton(builder);

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var categories = MaterialCategory.browsable();
    var slots = new ArrayList<SlotDefinition>(categories.size());

    for (var category : categories) {
      if (category == MaterialCategory.MISC) {
        continue;
      }

      var template = representativeItem(category);
      ClickHandler onCategory = ctx -> this.clickHandler.handle(ctx, category);
      var slot = SlotDefinition.of(-1, template, onCategory);

      slots.add(slot);
    }

    return slots;
  }

  private void registerBackButton(@NonNull MenuBuilder builder) {
    var backSlot = backButtonSlot();
    if (backSlot == null) {
      return;
    }

    builder.slot(backSlot.slot(), backSlot.template(), backSlot.handler());
  }

  private @Nullable SlotDefinition backButtonSlot() {
    var snap = this.config.value();
    var menuSpec = snap.menu();

    if (!menuSpec.categoryBackEnabled()) {
      return null;
    }

    var material = menuSpec.categoryBackMaterial();
    var name = menuSpec.categoryBackName();
    var lore = menuSpec.categoryBackLore();
    var loreArray = lore.toArray(String[]::new);

    var templateBuilder = ItemTemplate.builder(material);
    templateBuilder.name(name);
    templateBuilder.lore(loreArray);
    templateBuilder.italic(false);
    var template = templateBuilder.build();

    var slot = MaterialCategorySection.backSlot(menuSpec);

    return SlotDefinition.of(slot, template, this.clickHandler::back);
  }

  private @NonNull ItemTemplate representativeItem(@NonNull MaterialCategory category) {
    var snap = this.config.value();
    var menuSpec = snap.menu();

    var icon = category.icon();
    var categoryName = MaterialCategorySection.displayName(menuSpec, category);
    var name = MaterialCategorySection.itemName(menuSpec, categoryName);
    var lore = MaterialCategorySection.itemLore(menuSpec, categoryName);

    var builder = ItemTemplate.builder(icon);
    builder.name(name);
    builder.lore(lore);
    builder.italic(false);

    return builder.build();
  }
}
