package com.hanielcota.essentials.modules.hat;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.hat.command.HatCommand;
import com.hanielcota.essentials.modules.hat.config.HatConfig;
import com.hanielcota.essentials.modules.hat.service.HatService;
import lombok.NonNull;

public final class HatModule extends AbstractModule {

  public HatModule() {
    super("hat");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("hat", HatConfig.class, HatConfig::defaults);
    var service = new HatService();
    var command = new HatCommand(config, service);

    registrar.command(command);
  }
}
