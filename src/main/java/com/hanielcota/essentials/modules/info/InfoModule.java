package com.hanielcota.essentials.modules.info;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.info.command.InfoCommand;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.listener.InfoMenuCleanupListener;
import com.hanielcota.essentials.modules.info.menu.InfoMenu;
import com.hanielcota.essentials.modules.info.menu.InfoMenuState;
import com.hanielcota.essentials.modules.info.presentation.PlayerInfoEntries;
import com.hanielcota.essentials.modules.info.presentation.PluginInfoEntries;
import com.hanielcota.essentials.modules.info.presentation.ServerInfoEntries;
import com.hanielcota.essentials.modules.info.service.InfoService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.user.UserSessionService;

public final class InfoModule extends AbstractModule {

  public InfoModule() {
    super("info");
  }

  @Override
  protected void onEnable() {
    var config = config("info", InfoConfig.class, InfoConfig::defaults);
    var sessions = service(UserSessionService.class);
    var menus = service(MenuService.class);
    var players = service(PlayerProvider.class);

    var serverEntries = new ServerInfoEntries();
    var playerEntries = new PlayerInfoEntries(sessions, config);
    var pluginEntries = new PluginInfoEntries(plugin());
    var service = new InfoService(serverEntries, playerEntries, pluginEntries);

    var menuState = new InfoMenuState(players);
    var menu = new InfoMenu(config, service, menuState);
    registerMenu(menu);
    var cleanupListener = new InfoMenuCleanupListener(menuState, menus);
    registerListener(cleanupListener);

    var infoCommand = new InfoCommand(menuState, menus);
    registerCommand(infoCommand);
  }
}
