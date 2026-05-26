package com.hanielcota.essentials.modules.info.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.MenuService;
import com.github.hanielcota.menuframework.api.MenuSession;
import com.github.hanielcota.menuframework.definition.PaginationConfig;
import com.github.hanielcota.menuframework.definition.SlotDefinition;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.menu.presentation.InfoMenuRenderer;
import com.hanielcota.essentials.modules.info.menu.presentation.PlayerInfoEntries;
import com.hanielcota.essentials.modules.info.menu.presentation.PluginInfoEntries;
import com.hanielcota.essentials.modules.info.menu.presentation.ServerInfoEntries;
import com.hanielcota.essentials.shared.ComponentUtils;
import java.util.List;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Single /info menu with category and detail tabs. Switching tabs re-renders this same inventory
 * via {@link ClickContext#refresh()} — it never opens a separate menu, which keeps the framework's
 * navigation history and session intact.
 */
public final class InfoMenu implements EssentialsMenu {

  public static final String ID = "essentials.info";

  private final ConfigHandle<InfoConfig> config;
  private final InfoMenuState state;
  private final InfoMenuRenderer renderer;

  public InfoMenu(
      @NonNull ConfigHandle<InfoConfig> config,
      @NonNull ServerInfoEntries serverEntries,
      @NonNull PlayerInfoEntries playerEntries,
      @NonNull PluginInfoEntries pluginEntries,
      @NonNull InfoMenuState state) {
    this.config = config;
    this.state = state;
    this.renderer = new InfoMenuRenderer(config, serverEntries, playerEntries, pluginEntries);
  }

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var rows = snap.effectiveRows();
    var contentSlots = snap.effectiveContentSlots();

    var pagBuilder = PaginationConfig.builder();
    pagBuilder = pagBuilder.contentSlots(contentSlots);
    var pagination = pagBuilder.build();

    var rawTitle = snap.menuTitle();
    var menuTitle = ComponentUtils.mini(rawTitle);

    var menuBuilder = MenuFramework.builder(ID, menus);
    menuBuilder = menuBuilder.rows(rows);
    menuBuilder = menuBuilder.title(menuTitle);
    menuBuilder = menuBuilder.pagination(pagination);
    menuBuilder = menuBuilder.dynamicContent(this::buildSlots);

    var menu = menuBuilder.build();
    menu.register();
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
