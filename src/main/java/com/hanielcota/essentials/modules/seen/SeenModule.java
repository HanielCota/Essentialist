package com.hanielcota.essentials.modules.seen;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.seen.command.SeenCommand;
import com.hanielcota.essentials.modules.seen.config.SeenConfig;
import com.hanielcota.essentials.modules.seen.service.SeenService;

public final class SeenModule extends AbstractModule {

  public SeenModule() {
    super("seen");
  }

  @Override
  protected void onEnable() {
    var config = config("seen", SeenConfig.class, SeenConfig::defaults);
    var registry = context().services();
    var service = new SeenService(registry);

    registerCommand(new SeenCommand(config, service));
  }
}
