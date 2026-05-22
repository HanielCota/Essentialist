package com.hanielcota.essentials.modules.compact;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.compact.command.CompactCommand;
import com.hanielcota.essentials.modules.compact.config.CompactConfig;
import com.hanielcota.essentials.modules.compact.service.CompactService;

public final class CompactModule extends AbstractModule {

  public CompactModule() {
    super("compact");
  }

  @Override
  protected void onEnable() {
    var config = config("compact", CompactConfig.class, CompactConfig::defaults);
    var service = new CompactService(config);
    registerCommand(new CompactCommand(config, service));
  }
}
