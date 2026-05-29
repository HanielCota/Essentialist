package com.hanielcota.essentials.modules.essentials;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.menu.MenuViewCleanupListener;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.control.ModuleControl;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.essentials.command.EssentialsCommand;
import com.hanielcota.essentials.modules.essentials.config.EssentialsConfig;
import com.hanielcota.essentials.modules.essentials.menu.EssentialsModulesMenu;
import com.hanielcota.essentials.modules.essentials.menu.EssentialsModulesMenuState;
import com.hanielcota.essentials.modules.essentials.menu.ModulesMenuRenderer;
import lombok.NonNull;

public final class EssentialsModule extends AbstractModule {

  public EssentialsModule() {
    super("essentials");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("essentials", EssentialsConfig.class, EssentialsConfig::defaults);
    var configs = env.service(ConfigService.class);
    var menus = env.service(MenuService.class);
    var control = env.service(ModuleControl.class);

    var state = new EssentialsModulesMenuState();
    var renderer = new ModulesMenuRenderer(config, control);
    var menu = new EssentialsModulesMenu(config, state, renderer, control);
    registrar.menu(menu);

    var cleanupListener =
        new MenuViewCleanupListener(menus, EssentialsModulesMenu.ID, state::clear);
    registrar.listener(cleanupListener);

    var essentialsCommand = new EssentialsCommand(config, configs, menus);
    registrar.command(essentialsCommand);
  }
}
