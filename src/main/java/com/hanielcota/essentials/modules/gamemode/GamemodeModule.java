package com.hanielcota.essentials.modules.gamemode;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.gamemode.command.GamemodeCommand;
import com.hanielcota.essentials.modules.gamemode.config.GamemodeConfig;
import com.hanielcota.essentials.modules.gamemode.service.GamemodeService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;

public final class GamemodeModule extends AbstractModule {

  public GamemodeModule() {
    super("gamemode");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var gamemodeService = new GamemodeService();
    var config =
        registrar.configure(
            "gamemode", GamemodeConfig.class, GamemodeConfig::defaults, gamemodeService);

    var framework = env.service(PaperCommandFramework.class);
    var gamemodeCommand = new GamemodeCommand(config, gamemodeService, framework);

    registrar.command(gamemodeCommand);
  }
}
