package com.hanielcota.essentials.modules.whitelist;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.whitelist.command.WhitelistCommand;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.menu.WhitelistClickHandler;
import com.hanielcota.essentials.modules.whitelist.menu.WhitelistEntryRenderer;
import com.hanielcota.essentials.modules.whitelist.menu.WhitelistMenu;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import com.hanielcota.essentials.paper.PlayerProvider;

public final class WhitelistModule extends AbstractModule {

  public WhitelistModule() {
    super("whitelist");
  }

  @Override
  protected void onEnable() {
    var config = config("whitelist", WhitelistConfig.class, WhitelistConfig::defaults);
    var players = service(PlayerProvider.class);
    var service = new WhitelistService(players);
    var menus = service(MenuService.class);

    var renderer = new WhitelistEntryRenderer(config);
    var clickHandler = new WhitelistClickHandler(config, service);
    var menu = new WhitelistMenu(config, service, renderer, clickHandler);
    registerMenu(menu);
    var whitelistCommand = new WhitelistCommand(config, service, menus);
    registerCommand(whitelistCommand);
  }
}
