package com.hanielcota.essentials.modules.light;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.light.command.LightCommand;
import com.hanielcota.essentials.modules.light.config.LightConfig;
import com.hanielcota.essentials.modules.light.service.LightService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class LightModule extends AbstractModule {

  public LightModule() {
    super("light");
  }

  @Override
  protected void onEnable() {
    var config =
        configure("light", LightConfig.class, LightConfig::defaults, new LightService(plugin()));
    registerCommand(
        new LightCommand(
            config, service(LightService.class), service(PaperCommandFramework.class)));
  }
}
