package com.hanielcota.essentials.modules.ping;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.ping.command.PingCommand;
import com.hanielcota.essentials.modules.ping.config.PingConfig;
import com.hanielcota.essentials.modules.ping.service.PingService;

public final class PingModule extends AbstractModule {

  public PingModule() {
    super("ping");
  }

  @Override
  protected void onEnable() {
    var config = config("ping", PingConfig.class, PingConfig::defaults);
    var service = new PingService(config);
    registerCommand(new PingCommand(config, service));
  }
}
