package com.hanielcota.essentials.modules.smelt;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.smelt.command.SmeltCommand;
import com.hanielcota.essentials.modules.smelt.config.SmeltConfig;
import com.hanielcota.essentials.modules.smelt.service.SmeltService;

public final class SmeltModule extends AbstractModule {

  public SmeltModule() {
    super("smelt");
  }

  @Override
  protected void onEnable() {
    var config = config("smelt", SmeltConfig.class, SmeltConfig::defaults);
    SmeltService service = new SmeltService(config);
    registerService(SmeltService.class, service);
    registerCommand(new SmeltCommand(config, service));
  }
}
