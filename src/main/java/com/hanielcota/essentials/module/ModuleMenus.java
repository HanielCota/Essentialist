package com.hanielcota.essentials.module;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.menu.EssentialsMenu;
import lombok.NonNull;

final class ModuleMenus {

  void register(
      @NonNull MenuService menus,
      @NonNull EssentialsMenu menu,
      @NonNull ModuleCloseables closeables) {
    menu.register(menus);

    var menuId = menu.id();
    closeables.register(() -> menus.unregisterDefinition(menuId));
  }
}
