package com.hanielcota.essentials.modules.hat;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.hat.command.HatCommand;
import com.hanielcota.essentials.modules.hat.config.HatConfig;
import com.hanielcota.essentials.modules.hat.service.HatService;

public final class HatModule extends AbstractModule {

  public HatModule() {
    super("hat");
  }

  @Override
  protected void onEnable() {
    var config = config("hat", HatConfig.class, HatConfig::defaults);
    var service = new HatService();
    registerCommand(new HatCommand(config, service));
  }
}
