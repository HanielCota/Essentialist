package com.hanielcota.essentials.modules.kill;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.kill.command.KillCommand;
import com.hanielcota.essentials.modules.kill.config.KillConfig;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;

public final class KillModule extends AbstractModule {

  public KillModule() {
    super("kill");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("kill", KillConfig.class, KillConfig::defaults);
    var actors = env.service(ActorFactory.class);
    registrar.command(new KillCommand(config, actors));
  }
}
