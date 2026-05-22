package com.hanielcota.essentials.modules.info;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.info.command.InfoCommand;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.menu.InfoCategoryMenu;
import com.hanielcota.essentials.modules.info.menu.InfoMenu;
import com.hanielcota.essentials.modules.info.service.InfoService;

public final class InfoModule extends AbstractModule {

  public InfoModule() {
    super("info");
  }

  @Override
  protected void onEnable() {
    var config = config("info", InfoConfig.class, InfoConfig::defaults);
    var service = new InfoService(plugin());
    var menus = service(MenuService.class);

    registerMenu(new InfoMenu(config));
    registerMenu(
        new InfoCategoryMenu(
            InfoMenu.SERVER_ID,
            () -> config.value().serverTitle(),
            player -> service.serverEntries()));
    registerMenu(
        new InfoCategoryMenu(
            InfoMenu.PLAYER_ID, () -> config.value().playerTitle(), service::playerEntries));
    registerMenu(
        new InfoCategoryMenu(
            InfoMenu.ABOUT_ID,
            () -> config.value().aboutTitle(),
            player -> service.aboutEntries()));
    registerCommand(new InfoCommand(menus));
  }
}
