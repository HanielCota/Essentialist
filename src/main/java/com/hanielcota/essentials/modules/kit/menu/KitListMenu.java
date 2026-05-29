package com.hanielcota.essentials.modules.kit.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.config.KitListMenuConfig;
import com.hanielcota.essentials.modules.kit.menu.presentation.KitEntryRenderer;
import com.hanielcota.essentials.modules.kit.service.KitCatalog;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/** Paginated kits of the selected category, with state-aware icons and a back button. */
@RequiredArgsConstructor
public final class KitListMenu implements EssentialsMenu {

  public static final String ID = "essentials.kit.list";

  private static final int MIN_ROWS = 1;
  private static final int SLOTS_PER_ROW = 9;

  private final ConfigHandle<KitConfig> config;
  private final KitCatalog catalog;
  private final KitEntryRenderer renderer;
  private final KitMenuState state;
  private final KitListClickHandler clicks;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var cfg = this.config.value().listMenu();
    var rows = MenuLayouts.clampRows(cfg.rows());
    var title = ComponentUtils.mini(cfg.title());
    var backSlot = backSlot(cfg, rows);

    var contentSlots = contentSlots(cfg, rows, backSlot);
    var paginationBuilder = PaginationConfig.builder().contentSlots(contentSlots);
    if (rows > MIN_ROWS) {
      PageNavigation.apply(menus, paginationBuilder, ID, rows, cfg.navigation());
    }
    var pagination = paginationBuilder.build();

    var backTemplate = MenuTemplates.simple(cfg.backMaterial(), cfg.backName(), cfg.backLore());

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);
    builder.slot(backSlot, backTemplate, this.clicks::back);

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var uuid = player.getUniqueId();
    var categoryId = this.state.category(uuid);
    if (categoryId == null) {
      return List.of();
    }

    var kits = this.catalog.byCategory(categoryId);
    var slots = new ArrayList<SlotDefinition>(kits.size());
    for (var kit : kits) {
      var template = this.renderer.render(player, kit);
      var kitId = kit.id();

      slots.add(SlotDefinition.of(-1, template, click -> this.clicks.select(click, kitId)));
    }

    return slots;
  }

  private static int backSlot(@NonNull KitListMenuConfig cfg, int rows) {
    var fallback = (rows - 1) * SLOTS_PER_ROW;

    return MenuLayouts.sanitizeSlot(cfg.backSlot(), rows, fallback);
  }

  private static List<Integer> contentSlots(
      @NonNull KitListMenuConfig cfg, int rows, int backSlot) {
    var sanitized = MenuLayouts.sanitizeSlots(cfg.contentSlots(), rows);
    var navigation = cfg.navigation();
    var reserved =
        Set.of(
            backSlot, navigation.effectivePreviousSlot(rows), navigation.effectiveNextSlot(rows));

    return sanitized.stream().filter(slot -> !reserved.contains(slot)).toList();
  }
}
