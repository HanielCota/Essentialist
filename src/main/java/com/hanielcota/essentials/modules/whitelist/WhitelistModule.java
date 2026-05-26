package com.hanielcota.essentials.modules.whitelist;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.whitelist.command.WhitelistCommand;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.menu.WhitelistClickHandler;
import com.hanielcota.essentials.modules.whitelist.menu.WhitelistEntryRenderer;
import com.hanielcota.essentials.modules.whitelist.menu.WhitelistMenu;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

public final class WhitelistModule extends AbstractModule {

  public WhitelistModule() {
    super("whitelist");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("whitelist", WhitelistConfig.class, WhitelistConfig::defaults);
    var players = env.service(PlayerProvider.class);
    var service = new WhitelistService(players);
    var menus = env.service(MenuService.class);

    var renderer = new WhitelistEntryRenderer(config);
    var clickHandler = new WhitelistClickHandler(config, service);
    var menu = new WhitelistMenu(config, service, renderer, clickHandler);
    registrar.menu(menu);
    var whitelistCommand = new WhitelistCommand(config, service, menus);
    registrar.command(whitelistCommand);
  }
}
