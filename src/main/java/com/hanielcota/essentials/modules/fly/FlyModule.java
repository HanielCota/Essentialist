package com.hanielcota.essentials.modules.fly;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.fly.command.FlyCommand;
import com.hanielcota.essentials.modules.fly.config.FlyConfig;
import com.hanielcota.essentials.modules.fly.service.FlyService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class FlyModule extends AbstractModule {

  public FlyModule() {
    super("fly");
  }

  @Override
  protected void onEnable() {
    var config = configure("fly", FlyConfig.class, FlyConfig::defaults, new FlyService());
    registerCommand(
        new FlyCommand(config, service(FlyService.class), service(PaperCommandFramework.class)));
  }
}
