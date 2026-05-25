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
    var gamemodeService = new GamemodeService();
    var config =
        configure("gamemode", GamemodeConfig.class, GamemodeConfig::defaults, gamemodeService);

    var framework = service(PaperCommandFramework.class);
    var gamemodeCommand = new GamemodeCommand(config, gamemodeService, framework);

    registerCommand(gamemodeCommand);
  }
}
