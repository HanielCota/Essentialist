package com.hanielcota.essentials.modules.speed;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.speed.command.SpeedCommand;
import com.hanielcota.essentials.modules.speed.config.SpeedConfig;
import com.hanielcota.essentials.modules.speed.service.SpeedService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class SpeedModule extends AbstractModule {

  public SpeedModule() {
    super("speed");
  }

  @Override
  protected void onEnable() {
    var config = configure("speed", SpeedConfig.class, SpeedConfig::defaults, new SpeedService());
    registerCommand(
        new SpeedCommand(
            config, service(SpeedService.class), service(PaperCommandFramework.class)));
  }
}
