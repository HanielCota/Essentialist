package com.hanielcota.essentials.modules.kill;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.kill.command.KillCommand;
import com.hanielcota.essentials.modules.kill.config.KillConfig;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class KillModule extends AbstractModule {

  public KillModule() {
    super("kill");
  }

  @Override
  protected void onEnable() {
    var config = config("kill", KillConfig.class, KillConfig::defaults);
    registerCommand(new KillCommand(config, service(PaperCommandFramework.class)));
  }
}
