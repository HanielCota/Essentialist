package com.hanielcota.essentials.modules.kill;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.kill.command.KillCommand;
import com.hanielcota.essentials.modules.kill.config.KillConfig;
import com.hanielcota.essentials.modules.kill.service.KillService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class KillModule extends AbstractModule {

  public KillModule() {
    super("kill");
  }

  @Override
  protected void onEnable() {
    var config = configure("kill", KillConfig.class, KillConfig::defaults, new KillService());
    registerCommand(
        new KillCommand(config, service(KillService.class), service(PaperCommandFramework.class)));
  }
}
