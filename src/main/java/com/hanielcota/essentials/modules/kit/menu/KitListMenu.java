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
import com.hanielcota.essentials.modules.kit.domain.Kit;
import com.hanielcota.essentials.modules.kit.domain.KitSort;
import com.hanielcota.essentials.modules.kit.menu.presentation.KitEntryRenderer;
import com.hanielcota.essentials.modules.kit.service.KitCatalog;
import com.hanielcota.essentials.modules.kit.service.KitCooldownService;
import com.hanielcota.essentials.modules.kit.service.KitSortPreferences;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Paginated kits of the selected category: state-aware icons, a sort-cycle button and a back
 * button.
 */
@RequiredArgsConstructor
public final class KitListMenu implements EssentialsMenu {

  public static final String ID = "essentials.kit.list";

  private static final int MIN_ROWS = 1;
  private static final int SLOTS_PER_ROW = 9;

  private final ConfigHandle<KitConfig> config;
  private final KitCatalog catalog;
  private final KitEntryRenderer renderer;
  private final KitCooldownService cooldowns;
  private final KitMenuState state;
  private final KitSortPreferences sortPreferences;
  private final KitListClickHandler clicks;

  // Frozen at register() so the sort button stays aligned with the built inventory after a reload.
  private int registeredRows;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var cfg = this.config.value().listMenu();
    var rows = MenuLayouts.clampRows(cfg.rows());
    var title = ComponentUtils.mini(cfg.title());

    this.registeredRows = rows;

    var backSlot = backSlot(cfg, rows);
    var contentSlots = contentSlots(cfg, rows, backSlot, sortSlot(cfg, rows));
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

    var cfg = this.config.value().listMenu();
    var sort = this.sortPreferences.of(uuid);

    var kits = new ArrayList<>(this.catalog.byCategory(categoryId));
    sortKits(kits, sort, player);

    var slots = new ArrayList<SlotDefinition>(kits.size() + 1);
    for (var kit : kits) {
      var template = this.renderer.render(player, kit);
      var kitId = kit.id();

      slots.add(SlotDefinition.of(-1, template, click -> this.clicks.select(click, kitId)));
    }

    slots.add(sortButton(cfg, sort));
    return slots;
  }

  private void sortKits(@NonNull List<Kit> kits, @NonNull KitSort sort, @NonNull Player player) {
    var byName = Comparator.comparing(Kit::displayName, String.CASE_INSENSITIVE_ORDER);
    if (sort == KitSort.NAME) {
      kits.sort(byName);
      return;
    }

    Comparator<Kit> byAvailability =
        Comparator.comparingInt(kit -> isClaimable(player, kit) ? 0 : 1);
    kits.sort(byAvailability.thenComparing(byName));
  }

  private boolean isClaimable(@NonNull Player player, @NonNull Kit kit) {
    if (kit.hasPermission() && !player.hasPermission(kit.permission())) {
      return false;
    }
    if (kit.isEmpty()) {
      return false;
    }

    var uuid = player.getUniqueId();
    if (kit.oneTime() && this.cooldowns.hasClaimed(uuid, kit)) {
      return false;
    }

    return !(kit.hasCooldownGate() && this.cooldowns.remainingSeconds(uuid, kit) > 0);
  }

  private SlotDefinition sortButton(@NonNull KitListMenuConfig cfg, @NonNull KitSort sort) {
    var stateLabel = cfg.sortLabel(sort);
    var name = cfg.sortName().replace("{state}", stateLabel);
    var lore = expandSortLore(cfg, sort, stateLabel);

    var template = MenuTemplates.simple(cfg.sortMaterial(), name, lore);
    var slot = sortSlot(cfg, this.registeredRows);

    return SlotDefinition.of(slot, template, this.clicks::cycleSort);
  }

  private static List<String> expandSortLore(
      @NonNull KitListMenuConfig cfg, @NonNull KitSort sort, @NonNull String stateLabel) {
    var lore = new ArrayList<String>(cfg.sortLore().size() + KitSort.values().length);

    for (var line : cfg.sortLore()) {
      if (line.equals("{options}")) {
        appendOptions(lore, cfg, sort);
        continue;
      }

      lore.add(line.replace("{state}", stateLabel));
    }

    return lore;
  }

  private static void appendOptions(
      @NonNull List<String> lore, @NonNull KitListMenuConfig cfg, @NonNull KitSort active) {
    for (var sort : KitSort.values()) {
      var label = "<gray>" + cfg.sortLabel(sort);
      if (sort == active) {
        label += cfg.sortActiveMarker();
      }

      lore.add(label);
    }
  }

  private static int backSlot(@NonNull KitListMenuConfig cfg, int rows) {
    var fallback = (rows - 1) * SLOTS_PER_ROW;

    return MenuLayouts.sanitizeSlot(cfg.backSlot(), rows, fallback);
  }

  private static int sortSlot(@NonNull KitListMenuConfig cfg, int rows) {
    var fallback = rows * SLOTS_PER_ROW - 1;

    return MenuLayouts.sanitizeSlot(cfg.sortSlot(), rows, fallback);
  }

  private static List<Integer> contentSlots(
      @NonNull KitListMenuConfig cfg, int rows, int backSlot, int sortSlot) {
    var sanitized = MenuLayouts.sanitizeSlots(cfg.contentSlots(), rows);
    var navigation = cfg.navigation();
    var reserved =
        Set.of(
            backSlot,
            sortSlot,
            navigation.effectivePreviousSlot(rows),
            navigation.effectiveNextSlot(rows));

    return sanitized.stream().filter(slot -> !reserved.contains(slot)).toList();
  }
}
