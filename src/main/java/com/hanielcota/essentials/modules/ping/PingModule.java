package com.hanielcota.essentials.modules.ping;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.ping.command.PingCommand;
import com.hanielcota.essentials.modules.ping.config.PingConfig;
import com.hanielcota.essentials.modules.ping.service.PingService;
import lombok.NonNull;

public final class PingModule extends AbstractModule {

  public PingModule() {
    super("ping");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("ping", PingConfig.class, PingConfig::defaults);
    var service = new PingService(config);

    var command = new PingCommand(config, service);
    registrar.command(command);
  }
}
