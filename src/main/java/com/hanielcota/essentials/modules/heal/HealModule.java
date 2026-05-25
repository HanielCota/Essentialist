package com.hanielcota.essentials.modules.heal;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.heal.command.HealCommand;
import com.hanielcota.essentials.modules.heal.config.HealConfig;
import com.hanielcota.essentials.modules.heal.service.HealService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class HealModule extends AbstractModule {

  public HealModule() {
    super("heal");
  }

  @Override
  protected void onEnable() {
    var heal = new HealService();
    var config = configure("heal", HealConfig.class, HealConfig::defaults, heal);

    var service = service(HealService.class);
    var players = service(PlayerProvider.class);
    var framework = service(PaperCommandFramework.class);

    var command = new HealCommand(config, service, players, framework);
    registerCommand(command);
  }
}
