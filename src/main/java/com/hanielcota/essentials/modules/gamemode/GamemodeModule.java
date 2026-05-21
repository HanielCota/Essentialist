package com.hanielcota.essentials.modules.gamemode;

import com.hanielcota.essentials.command.ActorMessages;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.gamemode.command.GamemodeCommand;
import com.hanielcota.essentials.modules.gamemode.config.GamemodeConfig;
import com.hanielcota.essentials.modules.gamemode.service.GamemodeService;

public final class GamemodeModule extends AbstractModule {

  public GamemodeModule() {
    super("gamemode");
  }

  @Override
  protected void onEnable() {
    var config =
        configure(
            "gamemode", GamemodeConfig.class, GamemodeConfig::defaults, new GamemodeService());
    registerCommand(
        new GamemodeCommand(config, service(GamemodeService.class), service(ActorMessages.class)));
  }
}
