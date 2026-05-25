package com.hanielcota.essentials.modules.speed;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.speed.command.SpeedCommand;
import com.hanielcota.essentials.modules.speed.command.SpeedNotifier;
import com.hanielcota.essentials.modules.speed.config.SpeedConfig;
import com.hanielcota.essentials.modules.speed.service.SpeedService;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;

public final class SpeedModule extends AbstractModule {

  public SpeedModule() {
    super("speed");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var speed = new SpeedService();
    var config = registrar.configure("speed", SpeedConfig.class, SpeedConfig::defaults, speed);

    var actors = env.service(ActorFactory.class);
    var notifier = new SpeedNotifier(actors);

    var command = new SpeedCommand(config, speed, notifier);
    registrar.command(command);
  }
}
