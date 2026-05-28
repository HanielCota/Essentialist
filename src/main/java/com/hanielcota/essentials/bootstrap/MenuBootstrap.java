package com.hanielcota.essentials.bootstrap;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.core.ShutdownRegistry;
import com.hanielcota.essentials.core.ShutdownStep;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class MenuBootstrap implements BootstrapStage {

  private final EssentialsPlugin plugin;

  @Override
  public String name() {
    return "menu";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var services = context.services();
    var menuService = MenuFramework.create(this.plugin);
    services.register(MenuService.class, menuService);

    var shutdownRegistry = services.resolve(ShutdownRegistry.class);
    shutdownRegistry.register(ShutdownStep.of("MenuService", menuService::shutdown));
  }
}
