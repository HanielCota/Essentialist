package com.hanielcota.essentials.modules.fly;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.fly.command.FlyCommand;
import com.hanielcota.essentials.modules.fly.command.FlyNotifier;
import com.hanielcota.essentials.modules.fly.config.FlyConfig;
import com.hanielcota.essentials.modules.fly.listener.FlyGameModeListener;
import com.hanielcota.essentials.modules.fly.listener.FlyQuitListener;
import com.hanielcota.essentials.modules.fly.service.FlyService;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class FlyModule extends AbstractModule {

  public FlyModule() {
    super("fly");
  }

  @Override
  protected void onEnable() {
    var fly = new FlyService();
    var config = configure("fly", FlyConfig.class, FlyConfig::defaults, fly);
    var scheduler = service(Scheduler.class);

    var framework = service(PaperCommandFramework.class);
    var notifier = new FlyNotifier(config, framework);
    var flyCommand = new FlyCommand(fly, notifier);
    registerCommand(flyCommand);

    var gameModeListener = new FlyGameModeListener(scheduler, fly);
    registerListener(gameModeListener);

    var quitListener = new FlyQuitListener(fly);
    registerListener(quitListener);
  }
}
