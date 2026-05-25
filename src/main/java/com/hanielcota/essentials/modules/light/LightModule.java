package com.hanielcota.essentials.modules.light;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.light.command.LightCommand;
import com.hanielcota.essentials.modules.light.command.LightNotifier;
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
    var framework = service(PaperCommandFramework.class);

    var notifier = new LightNotifier(config, framework);
    var command = new LightCommand(light, notifier);
    var respawnListener = new LightRespawnListener(scheduler, light);
    var milkListener = new LightMilkListener(scheduler, light);

    registerCommand(command);
    registerListener(respawnListener);
    registerListener(milkListener);
  }
}
