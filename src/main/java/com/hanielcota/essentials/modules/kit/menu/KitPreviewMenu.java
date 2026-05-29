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
import com.hanielcota.essentials.modules.kit.config.KitPreviewMenuConfig;
import com.hanielcota.essentials.modules.kit.domain.Kit;
import com.hanielcota.essentials.modules.kit.menu.presentation.KitItemPreviewRenderer;
import com.hanielcota.essentials.modules.kit.service.KitCatalog;
import com.hanielcota.essentials.shared.ComponentUtils;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/** Read-only preview of the selected kit's items, plus a claim and a back button. */
@RequiredArgsConstructor
public final class KitPreviewMenu implements EssentialsMenu {

  public static final String ID = "essentials.kit.preview";

  private static final int MIN_ROWS = 1;
  private static final int SLOTS_PER_ROW = 9;

  private final ConfigHandle<KitConfig> config;
  private final KitCatalog catalog;
  private final KitMenuState state;
  private final KitPreviewClickHandler clicks;

  // Frozen at register() so the claim/back buttons stay aligned with the built inventory even if a
  // later /essentials reload changes the configured row count (same fix as the homes menus).
  private int registeredRows;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var cfg = this.config.value().previewMenu();
    var rows = MenuLayouts.clampRows(cfg.rows());
    var title = ComponentUtils.mini(cfg.title());

    this.registeredRows = rows;

    var contentSlots = contentSlots(cfg, rows);
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
    var cfg = this.config.value().previewMenu();
    var rows = this.registeredRows;

    var slots = new ArrayList<SlotDefinition>();
    appendItems(slots, player);
    appendClaim(slots, cfg, rows, player);
    appendBack(slots, cfg, rows);

    return slots;
  }

  private void appendItems(@NonNull List<SlotDefinition> slots, @NonNull Player player) {
    var kit = currentKit(player);
    if (kit == null) {
      return;
    }

    for (var item : kit.previewItems()) {
      var template = KitItemPreviewRenderer.render(item);

      slots.add(SlotDefinition.of(-1, template, click -> {}));
    }
  }

  private void appendClaim(
      @NonNull List<SlotDefinition> slots,
      @NonNull KitPreviewMenuConfig cfg,
      int rows,
      @NonNull Player player) {
    var kit = currentKit(player);
    var kitName = kit != null ? kit.displayName() : "?";

    var name = cfg.claimName().replace("{kit}", kitName);
    var lore = Placeholders.replaceInAll(cfg.claimLore(), "{kit}", kitName);
    var template = MenuTemplates.simple(cfg.claimMaterial(), name, lore);

    var slot = claimSlot(cfg, rows);
    slots.add(SlotDefinition.of(slot, template, this.clicks::claim));
  }

  private void appendBack(
      @NonNull List<SlotDefinition> slots, @NonNull KitPreviewMenuConfig cfg, int rows) {
    var template = MenuTemplates.simple(cfg.backMaterial(), cfg.backName(), cfg.backLore());
    var slot = backSlot(cfg, rows);

    slots.add(SlotDefinition.of(slot, template, this.clicks::back));
  }

  private Kit currentKit(@NonNull Player player) {
    var kitId = this.state.kit(player.getUniqueId());
    if (kitId == null) {
      return null;
    }

    return this.catalog.find(kitId).orElse(null);
  }

  private static int claimSlot(@NonNull KitPreviewMenuConfig cfg, int rows) {
    var fallback = rows * SLOTS_PER_ROW - 5;

    return MenuLayouts.sanitizeSlot(cfg.claimSlot(), rows, fallback);
  }

  private static int backSlot(@NonNull KitPreviewMenuConfig cfg, int rows) {
    var fallback = (rows - 1) * SLOTS_PER_ROW;

    return MenuLayouts.sanitizeSlot(cfg.backSlot(), rows, fallback);
  }

  private static List<Integer> contentSlots(@NonNull KitPreviewMenuConfig cfg, int rows) {
    var sanitized = MenuLayouts.sanitizeSlots(cfg.contentSlots(), rows);
    var navigation = cfg.navigation();
    var reserved =
        Set.of(
            claimSlot(cfg, rows),
            backSlot(cfg, rows),
            navigation.effectivePreviousSlot(rows),
            navigation.effectiveNextSlot(rows));

    return sanitized.stream().filter(slot -> !reserved.contains(slot)).toList();
  }
}
