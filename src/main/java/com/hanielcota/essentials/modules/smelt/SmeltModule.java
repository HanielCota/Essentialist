package com.hanielcota.essentials.modules.smelt;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.smelt.command.SmeltCommand;
import com.hanielcota.essentials.modules.smelt.config.SmeltConfig;
import com.hanielcota.essentials.modules.smelt.service.SmeltService;
import lombok.NonNull;

public final class SmeltModule extends AbstractModule {

  public SmeltModule() {
    super("smelt");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("smelt", SmeltConfig.class, SmeltConfig::defaults);
    var service = new SmeltService(config);
    registrar.command(new SmeltCommand(config, service));
  }
}
