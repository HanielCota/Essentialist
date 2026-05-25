package com.hanielcota.essentials.modules.list;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.list.command.ListCommand;
import com.hanielcota.essentials.modules.list.config.ListConfig;
import com.hanielcota.essentials.modules.list.menu.ListEntryRenderer;
import com.hanielcota.essentials.modules.list.menu.ListMenu;
import com.hanielcota.essentials.modules.list.service.ListService;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.paper.PlayerProvider;

public final class ListModule extends AbstractModule {

  public ListModule() {
    super("list");
  }

  @Override
  protected void onEnable() {
    var config = config("list", ListConfig.class, ListConfig::defaults);
    var players = service(PlayerProvider.class);
    var menus = service(MenuService.class);
    var registry = context().services();

    var service = new ListService(config, players, () -> registry.find(VanishService.class));
    var renderer = new ListEntryRenderer(config);
    var menu = new ListMenu(config, service, renderer);

    registerMenu(menu);
    registerCommand(new ListCommand(menus));
  }
}
