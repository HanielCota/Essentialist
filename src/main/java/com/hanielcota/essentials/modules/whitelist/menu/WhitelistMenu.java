package com.hanielcota.essentials.modules.whitelist.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import com.hanielcota.essentials.util.ComponentUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class WhitelistMenu implements EssentialsMenu {

  public static final String ID = "essentials.whitelist";

  private static final int MIN_ROWS = 1;

  private final ConfigHandle<WhitelistConfig> config;
  private final WhitelistService service;
  private final WhitelistEntryRenderer renderer;
  private final WhitelistClickHandler clickHandler;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var rows = snap.effectiveRows();
    var paginationBuilder = PaginationConfig.builder().contentSlots(snap.effectiveContentSlots());

    if (rows > MIN_ROWS) {
      PageNavigation.apply(menus, paginationBuilder, ID, rows, snap.navigation());
    }

    var pagination = paginationBuilder.build();

    var menuTitle = ComponentUtils.mini(snap.menuTitle());
    MenuFramework.builder(ID, menus)
        .rows(rows)
        .title(menuTitle)
        .pagination(pagination)
        .dynamicContent(this::buildSlots)
        .build()
        .register();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var whitelisted = this.service.list();
    if (whitelisted.isEmpty()) {
      return emptyState();
    }
    var slots = new ArrayList<SlotDefinition>(whitelisted.size());
    for (var entry : whitelisted) {
      var template = this.renderer.render(entry);
      var slotDef =
          SlotDefinition.of(-1, template, click -> this.clickHandler.handle(click, entry));

      slots.add(slotDef);
    }
    return slots;
  }

  /** A single placeholder item centred in the content area, shown when the whitelist is empty. */
  private List<SlotDefinition> emptyState() {
    var slots = this.config.value().effectiveContentSlots();
    var centerSlot = slots.get(slots.size() / 2);

    return List.of(SlotDefinition.of(centerSlot, this.renderer.renderEmpty(), click -> {}));
  }
}
