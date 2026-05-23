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
    var defaultValues = ClearConfig.defaults();
    var clearService = new ClearService();

    var configHandle = configure("clear", ClearConfig.class, () -> defaultValues, clearService);

    var activeService = service(ClearService.class);
    var commandFramework = service(PaperCommandFramework.class);

    var clearCommand = new ClearCommand(configHandle, activeService, commandFramework);
    registerCommand(clearCommand);
  }
}
