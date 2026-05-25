package com.hanielcota.essentials.modules.fly;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.fly.command.FlyCommand;
import com.hanielcota.essentials.modules.fly.command.FlyNotifier;
import com.hanielcota.essentials.modules.fly.config.FlyConfig;
import com.hanielcota.essentials.modules.fly.listener.FlyGameModeListener;
import com.hanielcota.essentials.modules.fly.listener.FlyQuitListener;
import com.hanielcota.essentials.modules.fly.service.FlyService;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;

public final class FlyModule extends AbstractModule {

  public FlyModule() {
    super("fly");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var fly = new FlyService();
    var config = registrar.configure("fly", FlyConfig.class, FlyConfig::defaults, fly);
    var scheduler = env.service(Scheduler.class);

    var framework = env.service(PaperCommandFramework.class);
    var notifier = new FlyNotifier(config, framework);
    var flyCommand = new FlyCommand(fly, notifier);
    registrar.command(flyCommand);

    var gameModeListener = new FlyGameModeListener(scheduler, fly);
    registrar.listener(gameModeListener);

    var quitListener = new FlyQuitListener(fly);
    registrar.listener(quitListener);
  }
}
