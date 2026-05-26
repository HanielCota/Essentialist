package com.hanielcota.essentials.module.lifecycle;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.menu.EssentialsMenu;
import lombok.NonNull;

public final class ModuleMenus {

  public void register(
      @NonNull MenuService menus,
      @NonNull EssentialsMenu menu,
      @NonNull ModuleCloseables closeables) {
    menu.register(menus);

    var menuId = menu.id();
    closeables.register(() -> menus.unregisterDefinition(menuId));
  }
}
