package com.hanielcota.essentials.bootstrap;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.service.ServiceRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class MenuBootstrap {

  private final EssentialsPlugin plugin;

  void register(@NonNull ServiceRegistry services) {
    var menuService = MenuFramework.create(this.plugin);
    services.register(MenuService.class, menuService);
  }
}
