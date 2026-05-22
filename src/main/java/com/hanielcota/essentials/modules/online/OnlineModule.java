package com.hanielcota.essentials.modules.online;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.online.command.OnlineCommand;
import com.hanielcota.essentials.modules.online.config.OnlineConfig;
import com.hanielcota.essentials.modules.online.menu.OnlineMenu;

public final class OnlineModule extends AbstractModule {

  public OnlineModule() {
    super("online");
  }

  @Override
  protected void onEnable() {
    var config = config("online", OnlineConfig.class, OnlineConfig::defaults);
    registerMenu(new OnlineMenu(config));
    registerCommand(new OnlineCommand(service(MenuService.class)));
  }
}
