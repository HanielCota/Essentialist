package com.hanielcota.essentials.modules.gamemode;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.gamemode.command.GamemodeCommand;
import com.hanielcota.essentials.modules.gamemode.config.GamemodeConfig;
import com.hanielcota.essentials.modules.gamemode.service.GamemodeService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class GamemodeModule extends AbstractModule {

  public GamemodeModule() {
    super("gamemode");
  }

  @Override
  protected void onEnable() {
    var newService = new GamemodeService();
    var config = configure("gamemode", GamemodeConfig.class, GamemodeConfig::defaults, newService);

    var gamemodeService = service(GamemodeService.class);
    var framework = service(PaperCommandFramework.class);
    var gamemodeCommand = new GamemodeCommand(config, gamemodeService, framework);

    registerCommand(gamemodeCommand);
  }
}
