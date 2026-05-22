package com.hanielcota.essentials.modules.clear;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.clear.command.ClearCommand;
import com.hanielcota.essentials.modules.clear.config.ClearConfig;
import com.hanielcota.essentials.modules.clear.service.ClearService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class ClearModule extends AbstractModule {

  public ClearModule() {
    super("clear");
  }

  @Override
  protected void onEnable() {
    var config = configure("clear", ClearConfig.class, ClearConfig::defaults, new ClearService());
    registerCommand(
        new ClearCommand(
            config, service(ClearService.class), service(PaperCommandFramework.class)));
  }
}
