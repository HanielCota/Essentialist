package com.hanielcota.essentials.modules.compact;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.compact.command.CompactCommand;
import com.hanielcota.essentials.modules.compact.config.CompactConfig;
import com.hanielcota.essentials.modules.compact.service.CompactService;
import lombok.NonNull;

public final class CompactModule extends AbstractModule {

  public CompactModule() {
    super("compact");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("compact", CompactConfig.class, CompactConfig::defaults);
    var service = new CompactService(config);
    var command = new CompactCommand(config, service);

    registrar.command(command);
  }
}
