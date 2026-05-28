package com.hanielcota.essentials.modules.info.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.PageNavigation;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.menu.presentation.InfoMenuRenderer;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Single /info menu with category and detail tabs. Switching tabs re-renders this same inventory
 * via {@link ClickContext#refresh()} — it never opens a separate menu, which keeps the framework's
 * navigation history and session intact.
 */
@RequiredArgsConstructor
public final class InfoMenu implements EssentialsMenu {

  public static final String ID = "essentials.info";

  private static final int MIN_ROWS = 1;

  private final @NonNull ConfigHandle<InfoConfig> config;
  private final @NonNull InfoMenuState state;
  private final @NonNull InfoMenuRenderer renderer;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var rows = snap.effectiveRows();
    var contentSlots = snap.effectiveContentSlots();
    var paginationBuilder = PaginationConfig.builder().contentSlots(contentSlots);
    if (rows > MIN_ROWS) {
      PageNavigation.apply(menus, paginationBuilder, ID, rows, snap.navigation());
    }
    var pagination = paginationBuilder.build();

    var rawTitle = snap.menuTitle();
    var menuTitle = ComponentUtils.mini(rawTitle);

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(menuTitle);
    builder.pagination(pagination);
    builder.dynamicContent(this::buildSlots);

    builder.buildAndRegister();
  }

  private List<SlotDefinition> buildSlots(@NonNull Player player, @NonNull MenuSession session) {
    var viewerId = player.getUniqueId();
    var tab = this.state.tab(viewerId);
    var target = this.state.resolveTarget(player);

    return this.renderer.slots(target, tab, this::switchTab);
  }

  private void switchTab(@NonNull ClickContext click, @NonNull InfoTab tab) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();

    this.state.switchTab(viewerId, tab);
    click.refresh();
  }
}
