package com.hanielcota.essentials.modules.spawnmob;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.spawnmob.command.SpawnMobCommand;
import com.hanielcota.essentials.modules.spawnmob.config.SpawnMobConfig;
import com.hanielcota.essentials.modules.spawnmob.service.SpawnMobService;
import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;

public final class SpawnMobModule extends AbstractModule {

  public SpawnMobModule() {
    super("spawnmob");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("spawnmob", SpawnMobConfig.class, SpawnMobConfig::defaults);
    var scheduler = env.service(Scheduler.class);

    var service = new SpawnMobService(scheduler);
    var spawnMobCommand = new SpawnMobCommand(config, service);
    registrar.command(spawnMobCommand);
  }
}
