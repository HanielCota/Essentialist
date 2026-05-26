package com.hanielcota.essentials.modules.info;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.info.command.InfoCommand;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.listener.InfoMenuCleanupListener;
import com.hanielcota.essentials.modules.info.menu.InfoMenu;
import com.hanielcota.essentials.modules.info.menu.InfoMenuState;
import com.hanielcota.essentials.modules.info.menu.presentation.PlayerInfoEntries;
import com.hanielcota.essentials.modules.info.menu.presentation.PluginInfoEntries;
import com.hanielcota.essentials.modules.info.menu.presentation.ServerInfoEntries;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.user.UserSessionService;
import lombok.NonNull;

public final class InfoModule extends AbstractModule {

  public InfoModule() {
    super("info");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("info", InfoConfig.class, InfoConfig::defaults);
    var sessions = env.service(UserSessionService.class);
    var menus = env.service(MenuService.class);
    var players = env.service(PlayerProvider.class);

    var serverEntries = new ServerInfoEntries();
    var playerEntries = new PlayerInfoEntries(sessions, config);
    var pluginEntries = new PluginInfoEntries(env.plugin());

    var menuState = new InfoMenuState(players);
    var menu = new InfoMenu(config, serverEntries, playerEntries, pluginEntries, menuState);
    registrar.menu(menu);
    var cleanupListener = new InfoMenuCleanupListener(menuState, menus);
    registrar.listener(cleanupListener);

    var infoCommand = new InfoCommand(menuState, menus);
    registrar.command(infoCommand);
  }
}
