package com.hanielcota.essentials.modules.list;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.list.command.ListCommand;
import com.hanielcota.essentials.modules.list.config.ListConfig;
import com.hanielcota.essentials.modules.list.menu.ListEntryRenderer;
import com.hanielcota.essentials.modules.list.menu.ListMenu;
import com.hanielcota.essentials.modules.list.service.ListService;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

public final class ListModule extends AbstractModule {

  public ListModule() {
    super("list");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("list", ListConfig.class, ListConfig::defaults);
    var players = env.service(PlayerProvider.class);
    var menus = env.service(MenuService.class);

    var service = new ListService(config, players, () -> env.findService(VanishService.class));
    var renderer = new ListEntryRenderer(config);
    var menu = new ListMenu(config, service, renderer);

    registrar.menu(menu);
    registrar.command(new ListCommand(menus));
  }
}
