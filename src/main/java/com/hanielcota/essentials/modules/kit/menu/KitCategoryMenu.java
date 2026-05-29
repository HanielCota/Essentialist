package com.hanielcota.essentials.modules.kit.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.domain.KitCategory;
import com.hanielcota.essentials.modules.kit.service.KitCatalog;
import com.hanielcota.essentials.shared.ComponentUtils;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/** The /kit landing menu: the categories that currently hold at least one kit, paginated. */
@RequiredArgsConstructor
public final class KitCategoryMenu implements EssentialsMenu {

  public static final String ID = "essentials.kit.categories";

  private static final int MIN_ROWS = 1;

  private final ConfigHandle<KitConfig> config;
  private final KitCatalog catalog;
  private final KitCategoryClickHandler clicks;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var cfg = this.config.value().categoryMenu();
    var rows = MenuLayouts.clampRows(cfg.rows());
    var title = ComponentUtils.mini(cfg.title());

    var contentSlots = contentSlots(MenuLayouts.sanitizeSlots(cfg.contentSlots(), rows), cfg, rows);
    var paginationBuilder = PaginationConfig.builder().contentSlots(contentSlots);
    if (rows > MIN_ROWS) {
      PageNavigation.apply(menus, paginationBuilder, ID, rows, cfg.navigation());
    }
    var pagination = paginationBuilder.build();

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var snap = this.config.value();
    var cfg = snap.categoryMenu();

    var slots = new ArrayList<SlotDefinition>();
    for (var category : shownCategories(snap)) {
      var kits = this.catalog.byCategory(category.id());
      if (kits.isEmpty()) {
        continue;
      }

      var template = categoryItem(cfg.itemName(), cfg.itemLore(), category, kits.size());
      var categoryId = category.id();

      slots.add(SlotDefinition.of(-1, template, click -> this.clicks.open(click, categoryId)));
    }

    return slots;
  }

  // Configured categories (sorted) that are in use, then any category referenced by a kit but not
  // configured (synthesised), so no kit is ever unreachable.
  private List<KitCategory> shownCategories(@NonNull KitConfig snap) {
    var used = new LinkedHashSet<String>();
    for (var kit : this.catalog.all()) {
      used.add(kit.category());
    }

    var shown = new ArrayList<KitCategory>();
    var seen = new java.util.HashSet<String>();
    for (var category : snap.sortedCategories()) {
      if (used.contains(category.id())) {
        shown.add(category);
        seen.add(category.id());
      }
    }
    for (var id : used) {
      if (!seen.contains(id)) {
        shown.add(snap.category(id));
      }
    }

    return shown;
  }

  private static ItemTemplate categoryItem(
      @NonNull String nameTemplate,
      @NonNull List<String> loreTemplate,
      @NonNull KitCategory category,
      int kitCount) {
    var name = nameTemplate.replace("{category}", category.displayName());

    var lore = new ArrayList<String>(loreTemplate.size());
    for (var line : loreTemplate) {
      lore.add(Placeholders.format(line, "category", category.displayName(), "kits", kitCount));
    }

    return MenuTemplates.simple(category.icon(), name, lore);
  }

  private static List<Integer> contentSlots(
      @NonNull List<Integer> sanitized,
      @NonNull com.hanielcota.essentials.modules.kit.config.KitCategoryMenuConfig cfg,
      int rows) {
    var navigation = cfg.navigation();
    var reserved =
        Set.of(navigation.effectivePreviousSlot(rows), navigation.effectiveNextSlot(rows));

    return sanitized.stream().filter(slot -> !reserved.contains(slot)).toList();
  }
}
