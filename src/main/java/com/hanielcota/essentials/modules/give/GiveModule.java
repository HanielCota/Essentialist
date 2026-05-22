package com.hanielcota.essentials.modules.give;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.give.command.GiveCommand;
import com.hanielcota.essentials.modules.give.config.GiveConfig;
import com.hanielcota.essentials.modules.give.service.GiveService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class GiveModule extends AbstractModule {

  public GiveModule() {
    super("give");
  }

  @Override
  protected void onEnable() {
    var config = configure("give", GiveConfig.class, GiveConfig::defaults, new GiveService());
    registerCommand(
        new GiveCommand(
            config,
            service(GiveService.class),
            service(PlayerProvider.class),
            service(PaperCommandFramework.class)));
  }
}
