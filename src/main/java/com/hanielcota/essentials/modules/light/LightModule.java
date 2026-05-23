package com.hanielcota.essentials.modules.light;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.light.command.LightCommand;
import com.hanielcota.essentials.modules.light.config.LightConfig;
import com.hanielcota.essentials.modules.light.listener.LightMilkListener;
import com.hanielcota.essentials.modules.light.listener.LightRespawnListener;
import com.hanielcota.essentials.modules.light.service.LightService;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class LightModule extends AbstractModule {

  public LightModule() {
    super("light");
  }

  @Override
  protected void onEnable() {
    var light = new LightService(plugin());
    var config = configure("light", LightConfig.class, LightConfig::defaults, light);
    var scheduler = service(Scheduler.class);

    registerCommand(new LightCommand(config, light, service(PaperCommandFramework.class)));
    registerListener(new LightRespawnListener(scheduler, light));
    registerListener(new LightMilkListener(scheduler, light));
  }
}
