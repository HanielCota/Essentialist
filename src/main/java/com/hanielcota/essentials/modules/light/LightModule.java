package com.hanielcota.essentials.modules.light;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.light.command.LightCommand;
import com.hanielcota.essentials.modules.light.command.LightNotifier;
import com.hanielcota.essentials.modules.light.config.LightConfig;
import com.hanielcota.essentials.modules.light.listener.LightMilkListener;
import com.hanielcota.essentials.modules.light.listener.LightRespawnListener;
import com.hanielcota.essentials.modules.light.service.LightService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;

public final class LightModule extends AbstractModule {

  public LightModule() {
    super("light");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var light = new LightService(env.plugin());
    var config = registrar.configure("light", LightConfig.class, LightConfig::defaults, light);
    var scheduler = env.service(Scheduler.class);
    var actors = env.service(ActorFactory.class);

    var notifier = new LightNotifier(config, actors);
    var command = new LightCommand(light, notifier);
    var respawnListener = new LightRespawnListener(scheduler, light);
    var milkListener = new LightMilkListener(scheduler, light);

    registrar.command(command);
    registrar.listener(respawnListener);
    registrar.listener(milkListener);
  }
}
