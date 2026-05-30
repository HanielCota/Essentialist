package com.hanielcota.essentials.modules.ban.menu;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The per-target ban builder: duration + reason buttons and confirm, all read from per-viewer
 * state.
 */
@RequiredArgsConstructor
public final class BanOptionsMenu implements EssentialsMenu {

  public static final String ID = "essentials.ban.options";

  private final ConfigHandle<BanConfig> config;
  private final BanOptionsView view;

  @Override
  public @NonNull String id() {
    return ID;
  }

  @Override
  public void register(@NonNull MenuService menus) {
    var snap = this.config.value();
    var menu = snap.menu();
    var rows = MenuLayouts.clampRows(menu.optionsRows());
    var title = ComponentUtils.mini(menu.optionsTitle());

    var builder = MenuFramework.builder(ID, menus);
    builder.rows(rows);
    builder.title(title);
    builder.dynamicContent(this.view::slotsFor);

    builder.buildAndRegister();
  }
}
